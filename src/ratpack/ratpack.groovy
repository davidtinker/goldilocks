import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import ratpack.form.Form
import tinker.mashtemp.App
import tinker.mashtemp.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

    System.setProperty("org.slf4j.simpleLogger.log.tinker", "debug")

    bindings {
        add(MarkupTemplateModule)
        add(MashTempModule)
        add(JacksonModule) { JacksonModule.Config c -> c.withMapper {
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            it.configure(SerializationFeature.INDENT_OUTPUT, true)
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        } }
    }

    handlers { App app ->
        get {
            render groovyMarkupTemplate("index.gtpl", app: app.state)
        }

        get("rest") {
            render(json(app.state))
        }

        post("vessel/:id") {
            app.updateVessel(parse(Form), pathTokens['id'])
            redirect('/')
        }

        assets("public")
    }
}

