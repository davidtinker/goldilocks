import tinker.mashtemp.AppService
import tinker.mashtemp.MashTempModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

import ratpack.groovy.templating.TemplatingModule

ratpack {

    bindings {
        add(TemplatingModule) { TemplatingModule.Config config -> config.staticallyCompile = false }
        add(MashTempModule)
    }

    handlers { AppService appService ->
        get {
            render groovyTemplate("layout.html", body: "index.html", cfg: appService.appConfig)
        }

        get("rest") {
            render "from the foo handler"
        }

        assets("public")
    }
}

