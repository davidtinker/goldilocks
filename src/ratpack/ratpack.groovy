import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.text.markup.TemplateConfiguration
import ratpack.form.Form
import tinker.goldilocks.App
import tinker.goldilocks.Html
import tinker.goldilocks.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.jackson.JacksonModule
import tinker.goldilocks.RaspberryPi
import tinker.goldilocks.TempLogRepo
import static tinker.goldilocks.Util.fixTemp

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

        get("refresh") {
            def s = app.state
            render(json([
                    time: Html.time(new Date()),
                    vessels: s.vessels.collect { v -> [
                            id: v.id,
                            temp: Html.temp(v.temp, s.fahrenheit)
                    ] }
            ]))
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
                if (v.tempProbe) ans.tempProbe = tr.list(v.tempProbe, ago, now, app.state.fahrenheit)
                if (v.heaterPin) ans.targetTemp = tr.list("target-" + v.heaterPin, ago, now, app.state.fahrenheit)
                render(json(ans))
            }
        }

        post("settings") {
            app.updateSettings(parse(Form))
            redirect('/')
        }

        post("vessel") {
            app.addVessel()
            redirect('/')
        }

        post("vessel/:id") {
            def f = parse(Form)
            def id = pathTokens['id'] as Integer
            if (f.action == 'Delete') app.deleteVessel(id)
            else app.updateVessel(f, id)
            redirect('/')
        }

        assets("public")
    }
}

