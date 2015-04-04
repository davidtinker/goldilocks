import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.text.markup.TemplateConfiguration
import ratpack.form.Form
import tinker.goldilocks.App
import tinker.goldilocks.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.groovy.template.TextTemplateModule
import ratpack.jackson.JacksonModule
import tinker.goldilocks.RaspberryPi
import tinker.goldilocks.TempLogRepo
import tinker.goldilocks.model.AppState
import tinker.goldilocks.model.Chart
import tinker.goldilocks.model.Item

import static ratpack.groovy.Groovy.context
import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.fromJson
import static ratpack.jackson.Jackson.json

ratpack {

    System.setProperty("org.slf4j.simpleLogger.log.tinker", "debug")

    bindings {
        add(MarkupTemplateModule) { TemplateConfiguration c ->
            c.autoNewLine = true
            c.autoIndent = true
        }
        add(TextTemplateModule)
        add(MashTempModule)
        add(JacksonModule) { JacksonModule.Config c -> c.withMapper {
            it.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            it.configure(SerializationFeature.INDENT_OUTPUT, true)
            it.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        } }
    }

    handlers { App app ->
        get {
            render groovyTemplate("index.html", app: app.state)
        }

        prefix("rest") {

            prefix("app") {
                handler("") {
                    context.byMethod {
                        get { render(json(app.state)) }
                        put { render(json(app.updateSettings(parse(fromJson(AppState))))) }
                    }
                }

                post("charts") {
                    render(json(app.addChart()))
                }

                put("charts/:id") {
                    Chart chart = parse(fromJson(Chart))
                    chart.id = Integer.parseInt(pathTokens['id'])
                    render(json(app.updateChart(chart)))
                }

                post("charts/:id/items") {
                    render(json(app.addItem(Integer.parseInt(pathTokens['id']))))
                }

                put("charts/:cid/items/:id") {
                    def item = parse(fromJson(Item))
                    item.id = Integer.parseInt(pathTokens['id'])
                    render(json(app.updateItem(Integer.parseInt(pathTokens['cid']), item)))
                }
            }

            get("pi") {
                def pi = registry.get(RaspberryPi)
                render(json([tempProbes: pi.tempProbes, pins: pi.pins]))
            }



            get("vessel/:id/history") {
                def vid = Integer.parseInt(pathTokens['id'])
                def v = app.state.charts.find { it.id == vid }
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

        post("chart/:chartId/item") {
            app.addItem(pathTokens['chartId'] as Integer)
            redirect('/')
        }

        post("chart/:chartId/item/:itemId") {
            def f = parse(Form)
            def chartId = pathTokens['chartId'] as Integer
            def itemId = pathTokens['itemId'] as Integer
            if (f.action == 'Delete') app.deleteItem(chartId, itemId)
            else app.updateItem(f, chartId, itemId)
            redirect('/')
        }

        assets("public")
    }
}

