package tinker.mashtemp

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.Scopes
import com.google.inject.name.Names
import groovy.util.logging.Slf4j

@Slf4j
class MashTempModule extends AbstractModule {

    @Override
    protected void configure() {
        File d = new File(System.getProperty("goldilocks.data", "/var/lib/goldilocks"))
        if (!d.directory) d.mkdirs()

        log.info("goldilocks.data [${d}]")
        bind(Key.get(File, Names.named("data.dir"))).toInstance(d)

        bind(RaspberryPi).to(FakeRaspberryPi)

        bind(App).in(Scopes.SINGLETON)
        bind(SetupRepo).in(Scopes.SINGLETON)
        bind(TempLogRepo).in(Scopes.SINGLETON)
    }
}
