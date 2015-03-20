package tinker.mashtemp

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.Scopes
import com.google.inject.name.Names

class MashTempModule extends AbstractModule {

    @Override
    protected void configure() {
        File f = new File("/etc", "mashtemp.json")
        if (!f.canWrite()) f = new File(System.getProperty("user.home"), "mashtemp.json")
        bind(Key.get(File, Names.named("config"))).toInstance(f)

        bind(RaspberryPi).to(FakeRaspberryPi)

        bind(App).in(Scopes.SINGLETON)
        bind(AppConfigRepo).in(Scopes.SINGLETON)
        bind(JsonService).in(Scopes.SINGLETON)
    }
}
