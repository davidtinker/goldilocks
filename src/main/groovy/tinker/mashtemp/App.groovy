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
        return state
    }

    synchronized void addVessel() {
        setupRepo.update { AppState s ->
            int max = 0
            s.vessels.each { if (it.id > max) max = it.id }
            s.vessels << new Vessel(id: max + 1)
        }
        refreshState()
    }

    synchronized void updateVessel(Map<String, Object> map, Integer id) {
        setupRepo.update { AppState s ->
            Vessel v = s.vessels.find { it.id == id }
            if (!v) throw new IllegalArgumentException("Vessel not found for id [${id}]")
            if (map.name) v.name = map.name
            if (map.tempProbe != null) v.tempProbe = map.tempProbe ?: null
            if (map.heaterPin != null) v.heaterPin = map.heaterPin ?: null
            if (map.targetTemp) v.targetTemp = map.targetTemp as Double
            if (map.heater) v.heater = map.heater
        }
        refreshState()
    }

    private synchronized AppState refreshState() throws IOException {
        def state = setupRepo.load()
        List<Callable> jobs = []
        state.vessels.each { v ->
            if (v.tempProbe) jobs << {
                try {
                    v.temp = pi.readTemp(v.tempProbe)
                    tempLogRepo.save(v.tempProbe, v.temp)
                } catch (Exception x) {
                    v.tempError = x.toString()
                    log.error(x.toString(), x)
                }
            }
            if (v.heaterPin) jobs << {
                double t = v.heater == "auto" && v.targetTemp ? v.targetTemp : 0.0
                tempLogRepo.save("target-" + v.heaterPin, t)
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
        return this.state = state
    }
}
