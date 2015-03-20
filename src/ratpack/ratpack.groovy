import tinker.mashtemp.App
import tinker.mashtemp.JsonService
import tinker.mashtemp.MashTempModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

import ratpack.groovy.templating.TemplatingModule

ratpack {

    bindings {
        add(TemplatingModule) { TemplatingModule.Config config -> config.staticallyCompile = false }
        add(MashTempModule)
    }

    handlers { App app ->
        get {
            render groovyTemplate("layout.html", body: "index.html", state: app.state)
        }

        get("rest") {
            render()
            def state = app.state
            registry.get(JsonService).toJson(state)
        }

        assets("public")
    }
}

