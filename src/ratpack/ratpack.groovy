import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.text.markup.TemplateConfiguration
import ratpack.form.Form
import tinker.goldilocks.App
import tinker.goldilocks.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.jackson.JacksonModule
import tinker.goldilocks.RaspberryPi
import tinker.goldilocks.TempLogRepo

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

        prefix("rest") {
            get {
                render(json(app.state))
            }

            get("vessel/:id/history") {
                def vid = Integer.parseInt(pathTokens['id'])
                def v = app.state.vessels.find { it.id == vid }
                def tr = registry.get(TempLogRepo)
                def ans = [:]
                GregorianCalendar gc = new GregorianCalendar()
                gc.add(Calendar.HOUR_OF_DAY, -2)
                def ago = gc.time
                def now = new Date()
                if (v.tempProbe) ans.tempProbe = tr.list(v.tempProbe, ago, now)
                if (v.heaterPin) ans.targetTemp = tr.list("target-" + v.heaterPin, ago, now)
                render(json(ans))
            }
        }

        post("vessel") {
            app.addVessel()
            redirect('/')
        }

        post("vessel/:id") {
            app.updateVessel(parse(Form), pathTokens['id'] as Integer)
            redirect('/')
        }

        assets("public")
    }
}

