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

    private Runnable updateState = {
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
        pool.scheduleAtFixedRate(updateState, 0, 10, TimeUnit.SECONDS)
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

    private AppState refreshState() throws IOException {
        def state = repo.load()
        List<Callable> jobs = []
        for (Vessel v : state.vessels) {
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
                    v.heaterOn = pi.getPin(v.heaterPin)
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
