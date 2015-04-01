package tinker.goldilocks

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.inject.name.Named
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import tinker.goldilocks.model.AppState

import javax.inject.Inject

/**
 * Repository for our configuration. Stores data in json file.
 */
@CompileStatic
@Slf4j
class SetupRepo {

    private final File dataDir
    private final File file
    private final ObjectMapper objectMapper

    @Inject
    SetupRepo(@Named("data.dir") File dataDir, ObjectMapper objectMapper) {
        this.dataDir = dataDir
        this.file = new File(dataDir, "setup.json")
        this.objectMapper = objectMapper
    }

    File getDataDir() { return dataDir }

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
        def bak = new File(file.absolutePath + ".bak")
        bak.delete()
        if (file.exists() && !file.renameTo(bak)) throw new IOException("Unable to rename ${file} to ${bak}")
        n.renameTo(file)
        bak.delete()
    }

}
