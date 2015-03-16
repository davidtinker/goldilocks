package tinker.mashtemp

import com.google.inject.AbstractModule
import com.google.inject.Key
import com.google.inject.Scopes
import com.google.inject.name.Names

class MashTempModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Key.get(File, Names.named("config"))).toInstance(new File("mashtemp.json"))

        bind(AppConfigRepo).in(Scopes.SINGLETON)
        bind(AppService).in(Scopes.SINGLETON)
        bind(JsonService).in(Scopes.SINGLETON)
        bind(RaspberryPi).to(FakeRaspberryPi)
    }
}
