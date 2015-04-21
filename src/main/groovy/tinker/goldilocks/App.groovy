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
    private Map<String, PidTempController> pids = new HashMap<>()

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(8)

    private Runnable updateTask = {
        try {
            updateState()
        } catch (Exception x) {
            log.error(x.toString(), x)
        }
    }

    @Inject
    App(SetupRepo setupRepo, TempLogRepo tempLogRepo, RaspberryPi pi) {
        this.setupRepo = setupRepo
        this.tempLogRepo = tempLogRepo
        this.pi = pi
        state = setupRepo.load()
        pool.scheduleAtFixedRate(updateTask, 0, 10, TimeUnit.SECONDS)
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

    synchronized AppState updateControl(Integer chartId, Control n) {
        setupRepo.update { AppState s ->
            Control c = s.findChart(chartId).findControl(n.id);
            if (n.name) c.name = n.name
            if (n.tempProbe != null) c.tempProbe = n.tempProbe ?: null
            if (n.pin != null) c.pin = n.pin ?: null
            if (n.color != null) c.color = n.color ?: null
            if (n.targetTemp) c.targetTemp = n.targetTemp as Double
            if (n.pinState) c.pinState = n.pinState
        }
        return refreshState()
    }

    synchronized AppState deleteControl(Integer chartId, Integer controlId) {
        setupRepo.update { AppState s ->
            def c = s.findChart(chartId)
            def i = c.findControl(controlId)
            if (i.pin) pi.setPin(i.pin, false)
            c.controls.remove(i)
        }
        return refreshState()
    }

    private synchronized AppState refreshState() throws IOException {
        def state = setupRepo.load()
        List<Callable> jobs = []
        state.charts.each { c ->
            c.controls.each { i ->
                i.errors = []
                if (i.tempProbe) jobs << {
                    try {
                        i.temp = pi.readTemp(i.tempProbe)
                        tempLogRepo.save(i.tempProbe, i.temp)
                    } catch (Exception x) {
                        i.errors << "Unable to read temp probe ${i.tempProbe}: " + x
                        log.error(x.toString(), x)
                    }
                }
                if (i.pin) jobs << {
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

        if (jobs) pool.invokeAll(jobs)
        state.updated = new Date()
        return this.state = state
    }

    private synchronized void updateState() {
        def state = refreshState()
        Map<String, PidTempController> newPids = new HashMap<>()
        state.charts.each { c ->
            c.controls.each { i ->
                if (i.tempProbe && i.pin && i.pinState == "auto" && i.targetTemp) {
                    String key = "${c.id} ${i.id} ${i.kc} ${i.ti} ${i.td}"
                    def pid = pids.get(key)
                    if (!pid) pid = new PidTempController(i.kc, i.ti, i.id, 10.0)
                    newPids.put(key, pid)
                    double yk = pid.update(i.temp, i.targetTemp)
                    log.debug("yk " + yk)
                    pi.setPin(i.pin, yk >= 50.0)
                }
            }
        }
        pids = newPids
    }
}
