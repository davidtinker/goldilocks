package tinker.mashtemp

import com.google.inject.name.Named
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import tinker.mashtemp.model.AppConfig

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
    }

    /**
     * Load configuration from our file. Returns new empty configuration if the file does not exist.
     */
    AppConfig load() {
        if (!file.exists()) return new AppConfig()
        return jsonService.fromJson(file.text, AppConfig)
    }

    /**
     * Load the configuration and pass it to the closure. Any changes made are saved when the closure finishes.
     */
    synchronized void update(Closure<AppConfig> c) {
        def cfg = load()
        c(cfg)
        file.text = jsonService.toJson(c)
    }

}
