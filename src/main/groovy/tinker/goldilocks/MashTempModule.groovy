package tinker.goldilocks

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

        boolean fakePi = Boolean.getBoolean("fakePi")

        log.info("goldilocks.data [${d}] fakePi=${fakePi}")
        bind(Key.get(File, Names.named("data.dir"))).toInstance(d)

        bind(RaspberryPi).to(fakePi ? FakeRaspberryPi : RaspberryPiImpl).in(Scopes.SINGLETON)

        bind(App).in(Scopes.SINGLETON)
        bind(SetupRepo).in(Scopes.SINGLETON)
        bind(TempLogRepo).in(Scopes.SINGLETON)
    }
}
