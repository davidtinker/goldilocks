package tinker.mashtemp

import groovy.util.logging.Slf4j
import tinker.mashtemp.model.AppState
import tinker.mashtemp.model.Vessel

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
class App {

    private final AppConfigRepo repo
    private final RaspberryPi pi

    private volatile AppState state

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(8)

    private Runnable updateTask = {
        try {
            state = refreshState()
        } catch (Exception x) {
            log.error(x.toString(), x)
        }
    }

    @Inject
    App(AppConfigRepo repo, RaspberryPi pi) {
        this.repo = repo
        this.pi = pi
        state = repo.load()
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
        return state
    }

    void updateVessel(Map<String, Object> map, String id) {
        repo.update { AppState s ->
            Vessel v = s.vessels.find { it.id == id }
            if (!v) throw new IllegalArgumentException("Vessel not found for id [${id}]")
            if (map.name) v.name = map.name
            if (map.heater) v.heater = map.heater
            if (map.targetTemp) v.targetTemp = map.targetTemp as Double
        }
        refreshState()
    }

    private synchronized AppState refreshState() throws IOException {
        def state = repo.load()
        List<Callable> jobs = []
        state.vessels.each { v ->
            if (v.tempProbeId) jobs << {
                try {
                    v.temp = pi.readTemp(v.tempProbeId)
                } catch (Exception x) {
                    v.tempError = x.toString()
                    log.error(x.toString(), x)
                }
            }
            if (v.heaterPin) jobs << {
                try {
                    pi.setPin(v.heaterPin, v.heater == "on")
                } catch (Exception x) {
                    v.heaterError = x.toString()
                    log.error(x.toString(), x)
                }
            }
        }
        if (jobs) pool.invokeAll(jobs)
        state.updated = new Date()
        return state
    }
}
