package tinker.mashtemp

import com.google.inject.name.Named
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import tinker.mashtemp.model.AppState

import javax.inject.Inject

/**
 * Repository for our configuration. Stores data in json file.
 */
@CompileStatic
@Slf4j
class AppConfigRepo {

    private final File file
    private final JsonService jsonService

    @Inject
    AppConfigRepo(@Named("config") File file, JsonService jsonService) {
        this.file = file
        this.jsonService = jsonService
        log.info("Loading configuration from " + file.absolutePath + (file.exists() ? "" : " (does not exist)"))
    }

    /**
     * Load configuration from our file. Returns new empty configuration if the file does not exist.
     */
    AppState load() {
        if (!file.exists()) return new AppState()
        return jsonService.fromJson(file.text, AppState)
    }

    /**
     * Load the configuration and pass it to the closure. Any changes made are saved when the closure finishes.
     */
    synchronized void update(Closure<AppState> c) {
        def cfg = load()
        c(cfg)
        file.text = jsonService.toJson(c)
    }

}
