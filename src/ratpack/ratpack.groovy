import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.text.markup.TemplateConfiguration
import ratpack.form.Form
import tinker.mashtemp.App
import tinker.mashtemp.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.jackson.JacksonModule
import tinker.mashtemp.RaspberryPi

import static ratpack.groovy.Groovy.groovyMarkupTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.json

ratpack {

    System.setProperty("org.slf4j.simpleLogger.log.tinker", "debug")

    bindings {
        add(MarkupTemplateModule) { TemplateConfiguration c ->
            c.autoNewLine = true
            c.autoIndent = true
        }
        add(MashTempModule)
        add(JacksonModule) { JacksonModule.Config c -> c.withMapper {
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            it.configure(SerializationFeature.INDENT_OUTPUT, true)
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        } }
    }

    handlers { App app ->
        get {
            def pi = registry.get(RaspberryPi)
            render groovyMarkupTemplate("index.gtpl", app: app.state, tempProbes: pi.listTempProbes(), pins: pi.listPins())
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

