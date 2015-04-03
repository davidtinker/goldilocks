package tinker.goldilocks

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import tinker.goldilocks.model.AppState
import tinker.goldilocks.model.Item

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

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(8)

    private Runnable updateTask = {
        try {
            refreshState()
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

    synchronized AppState addChart() {
        setupRepo.update { AppState s -> s.addChart() }
        return refreshState()
    }

    synchronized void deleteChart(Integer chartId) {
        setupRepo.update { AppState s ->
            def c = s.findChart(chartId)
            if (c.items) throw new IllegalArgumentException("Chart ${chartId} still has items")
            s.charts.remove(c)
        }
        refreshState()
    }

    synchronized void addItem(Integer chartId) {
        setupRepo.update { AppState s -> s.findChart(chartId).addItem() }
        refreshState()
    }

    synchronized void updateItem(Map<String, Object> map, Integer chartId, Integer itemId) {
        setupRepo.update { AppState s ->
            Item i = s.findChart(chartId).findItem(itemId);
            if (map.name) i.name = map.name
            if (map.tempProbe != null) i.tempProbe = map.tempProbe ?: null
            if (map.pin != null) i.pin = map.pin ?: null
            if (map.colorScheme != null) i.colorScheme = map.colorScheme ?: null
            if (map.targetTemp) i.targetTemp = map.targetTemp as Double
            if (map.pinState) i.pinState = map.pinState
        }
        refreshState()
    }

    synchronized void deleteItem(Integer chartId, Integer itemId) {
        setupRepo.update { AppState s ->
            def c = s.findChart(chartId)
            def i = c.findItem(itemId)
            if (i.pin) pi.setPin(i.pin, false)
            c.items.remove(i)
        }
        refreshState()
    }

    private synchronized AppState refreshState() throws IOException {
        def state = setupRepo.load()
        List<Callable> jobs = []
        state.charts.each { c ->
            c.items.each { i ->
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
                    if (i.pinState == "auto") tempLogRepo.save("target-" + i.pin, i.targetTemp)
                    try {
                        pi.setPin(i.pin, i.pinState == "on")
                    } catch (Exception x) {
                        i.errors << "Unable to set heater pin ${i.pin} to ${i.pinState}: " + x
                        log.error(x.toString(), x)
                    }
                }
            }

        }

        if (jobs) pool.invokeAll(jobs)
        state.updated = new Date()
        return this.state = state
    }
}
