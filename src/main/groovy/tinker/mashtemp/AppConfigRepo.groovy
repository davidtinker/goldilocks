package tinker.mashtemp

import com.fasterxml.jackson.databind.ObjectMapper
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
    private final ObjectMapper objectMapper

    @Inject
    AppConfigRepo(@Named("config") File file, ObjectMapper objectMapper) {
        this.file = file
        this.objectMapper = objectMapper
        log.info("Loading configuration from " + file.absolutePath + (file.exists() ? "" : " (does not exist)"))
    }

    /**
     * Load configuration from our file. Returns new empty configuration if the file does not exist.
     */
    synchronized AppState load() {
        if (!file.exists()) return new AppState()
        return objectMapper.readValue(file, AppState)
    }

    /**
     * Load the configuration and pass it to the closure. Any changes made are saved when the closure finishes.
     */
    synchronized void update(Closure c) {
        def cfg = load()
        c(cfg)
        File n = new File(file.absolutePath + ".new")
        if (n.exists() && !n.delete()) throw new IOException("Unable to delete [${n}]")
        objectMapper.writeValue(n, cfg)
        file.renameTo(new File(file.absolutePath + ".bak"))
        n.renameTo(file)
    }

}
