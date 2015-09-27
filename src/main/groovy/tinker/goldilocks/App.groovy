package tinker.goldilocks

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import tinker.goldilocks.model.AppState
import tinker.goldilocks.model.AppTimer
import tinker.goldilocks.model.Chart
import tinker.goldilocks.model.Control

import javax.annotation.PreDestroy
import javax.inject.Inject
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * All the operations our app supports. Reads data from the temp probes etc. and updates the state of the app
 * in the background.
 */
@Slf4j
@CompileStatic
class App {

    private final SetupRepo setupRepo
    private final TempLogRepo tempLogRepo
    private final RaspberryPi pi

    private volatile AppState state
    private Map<String, TempController> tempControllers = new HashMap<>()

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(8)

    @Inject
    App(SetupRepo setupRepo, TempLogRepo tempLogRepo, RaspberryPi pi) {
        this.setupRepo = setupRepo
        this.tempLogRepo = tempLogRepo
        this.pi = pi
        state = setupRepo.load()

        pool.scheduleAtFixedRate({
            try {
                refreshState()
            } catch (Exception x) {
                log.error(x.toString(), x)
            }
        }, 0, 5, TimeUnit.SECONDS)

        pool.scheduleAtFixedRate({
            try {
                updateTempControllers()
            } catch (Exception x) {
                log.error(x.toString(), x)
            }
        }, 0, 1, TimeUnit.SECONDS)
    }

    @PreDestroy
    private void destroy() {
        pool.shutdownNow()
    }

    /**
     * Get the most recent state of the app with all temps etc filled in.
     */
    AppState getState() {
        state.updated = new Date();
        return state
    }

    synchronized AppState updateSettings(AppState n) {
        setupRepo.update { AppState s ->
            if (n.title != null) s.title = n.title
            if (n.fahrenheit != null) s.fahrenheit = n.fahrenheit
        }
        return refreshState()
    }

    synchronized AppState addOrUpdateTimer(AppTimer n) {
        setupRepo.update { AppState s ->
            AppTimer t = n.id ? s.findTimer(n.id) : s.addTimer()
            if (n.name != null) t.name = n.name
            if (n.seconds != null) {
                t.seconds = n.seconds
                GregorianCalendar gc = new GregorianCalendar()
                gc.add(Calendar.SECOND, n.seconds)
                t.expires = gc.time
            }
        }
        return refreshState()
    }

    synchronized AppState deleteTimer(Integer timerId) {
        setupRepo.update { AppState s ->
            s.timers.remove(s.findTimer(timerId))
        }
        return refreshState()
    }

    synchronized AppState addChart() {
        setupRepo.update { AppState s -> s.addChart() }
        return refreshState()
    }

    synchronized AppState updateChart(Chart n) {
        setupRepo.update { AppState s ->
            Chart c = s.findChart(n.id)
            if (n.minutes != null) c.minutes = n.minutes
        }
        return refreshState()
    }

    synchronized AppState deleteChart(Integer chartId) {
        setupRepo.update { AppState s ->
            s.charts.remove(s.findChart(chartId))
        }
        return refreshState()
    }

    synchronized AppState addControl(Integer chartId) {
        setupRepo.update { AppState s -> s.findChart(chartId).addControl() }
        return refreshState()
    }

    synchronized AppState updateControls(List<Control> list) {
        setupRepo.update { AppState s ->
            list.each { n ->
                Control c = s.findControl(n.id);
                if (n.name) c.name = n.name
                if (n.tempProbe != null) c.tempProbe = n.tempProbe ?: null
                if (n.pin != null) c.pin = n.pin ?: null
                if (n.color != null) c.color = n.color ?: null
                if (n.targetTemp) c.targetTemp = n.targetTemp as Double
                if (n.pinState) c.pinState = n.pinState
                if (n.gainPerMin != null) c.gainPerMin = n.gainPerMin
                if (n.lagPeriodSecs != null) c.lagPeriodSecs = n.lagPeriodSecs
                if (n.autoTune != null) c.autoTune = n.autoTune
            }
        }
        return refreshState()
    }

    synchronized AppState deleteControl(String controlId) {
        setupRepo.update { AppState s ->
            def i = s.findControl(controlId)
            if (i.pin) pi.setPin(i.pin, false)
            s.charts.each { it.controls.remove(i) }
        }
        return refreshState()
    }

    private synchronized AppState refreshState() throws IOException {
        def state = setupRepo.load()
        state.charts.each { c ->
            c.controls.each { i ->
                i.errors = []
                if (i.tempProbe) {
                    try {
                        i.temp = pi.readTemp(i.tempProbe)
                        tempLogRepo.save(i.tempProbe, i.temp)
                    } catch (Exception x) {
                        i.errors << "Unable to read temp probe ${i.tempProbe}: " + x
                        log.error(x.toString(), x)
                    }
                }
                if (i.pin) {
                    if (i.tempProbe) {
                        tempLogRepo.save("target-" + i.pin, i.pinState == "auto" && i.targetTemp != null ? i.targetTemp : (Double)0.0)
                    }
                    if (i.pinState != "auto") {
                        try {
                            pi.setPin(i.pin, i.pinState == "on")
                        } catch (Exception x) {
                            i.errors << "Unable to set pin ${i.pin} to ${i.pinState}: " + x
                            log.error(x.toString(), x)
                        }
                    }
                    try {
                        i.pinOn = pi.getPin(i.pin)
                    } catch (Exception x) {
                        i.errors << "Unable to read pin ${i.pin}: " + x
                        log.error(x.toString(), x)
                    }
                }
            }

        }
        state.updated = new Date()
        return this.state = state
    }

    private void updateTempControllers() {
        def state = this.state
        if (!state) return

        Map<String, TempController> newTCs = new HashMap<>()
        List<Control> changed = []
        state.charts.each { c ->
            c.controls.each { i ->
                if (i.tempProbe && i.pin && i.temp != null && i.pinOn != null) {
                    def tc = tempControllers.get(i.id)
                    if (!tc) tc = new TempController()
                    tc.gainPerMin = i.gainPerMin ?: (double)0.7
                    tc.lagPeriodSecs = i.lagPeriodSecs ?: 120
                    tc.autoTune = i.autoTune == null || i.autoTune
                    newTCs.put(i.id, tc)

                    boolean heaterOn = tc.tick(i.temp, i.pinOn ?: false, i.pinState == "auto" ? i.targetTemp : null)
                    if (i.pinState == "auto" && i.targetTemp) pi.setPin(i.pin, heaterOn)

                    if (tc.gainPerMin != i.gainPerMin || tc.lagPeriodSecs != i.lagPeriodSecs) {
                        i.gainPerMin = tc.gainPerMin
                        i.lagPeriodSecs = tc.lagPeriodSecs
                        changed << new Control(id: i.id, gainPerMin: tc.gainPerMin, lagPeriodSecs: tc.lagPeriodSecs)
                    }
                }
            }
        }
        tempControllers = newTCs

        // this might take a little while and we don't want to miss temp controller ticks so do it in the background
        if (changed) pool.submit({ updateControls(changed) })
    }
}
