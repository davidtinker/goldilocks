import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.SerializationFeature
import groovy.text.markup.TemplateConfiguration
import tinker.goldilocks.App
import tinker.goldilocks.MashTempModule

import ratpack.groovy.template.MarkupTemplateModule
import ratpack.groovy.template.TextTemplateModule
import ratpack.jackson.JacksonModule
import tinker.goldilocks.RaspberryPi
import tinker.goldilocks.TempLogRepo
import tinker.goldilocks.model.AppState
import tinker.goldilocks.model.AppTimer
import tinker.goldilocks.model.Chart
import tinker.goldilocks.model.Control

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

                post("timers") {
                    render(json(app.addOrUpdateTimer(parse(fromJson(AppTimer)))))
                }

                handler("timers/:id") {
                    context.byMethod {
                        put {
                            AppTimer timer = parse(fromJson(AppTimer))
                            timer.id = Integer.parseInt(pathTokens['id'])
                            render(json(app.addOrUpdateTimer(timer)))
                        }
                        delete {
                            render(json(app.deleteTimer(Integer.parseInt(pathTokens['id']))))
                        }
                    }
                }

                post("charts") {
                    render(json(app.addChart()))
                }

                handler("charts/:id") {
                    context.byMethod {
                        put {
                            Chart chart = parse(fromJson(Chart))
                            chart.id = Integer.parseInt(pathTokens['id'])
                            render(json(app.updateChart(chart)))
                        }
                        delete {
                            render(json(app.deleteChart(Integer.parseInt(pathTokens['id']))))
                        }
                    }
                }

                post("charts/:id/controls") {
                    render(json(app.addControl(Integer.parseInt(pathTokens['id']))))
                }

                handler("charts/:cid/controls/:id") {
                    context.byMethod {
                        put {
                            def control = parse(fromJson(Control))
                            control.id = pathTokens['id']
                            render(json(app.updateControl(control)))
                        }
                        delete {
                            render(json(app.deleteControl(pathTokens['id'])))
                        }
                    }
                }

                get("charts/:cid/history") {
                    def chart = app.state.findChart(Integer.parseInt(pathTokens['cid']))
                    def tr = registry.get(TempLogRepo)
                    int minutes = request.queryParams.minutes as Integer ?: 60
                    GregorianCalendar gc = new GregorianCalendar()
                    gc.add(Calendar.MINUTE, -minutes)
                    def ago = gc.time
                    def now = new Date()
                    def ans = chart.controls.findAll { it.tempProbe }.collect { c ->
                        def dto = [id: c.id, name: c.name, color: c.color]
                        if (c.tempProbe) dto.tempProbe = tr.list(c.tempProbe, ago, now)
                        if (c.pin) dto.targetTemp = tr.list("target-" + c.pin, ago, now)
                        return dto
                    }
                    render(json(ans))
                }
            }

            get("pi") {
                def pi = registry.get(RaspberryPi)
                render(json([tempProbes: pi.tempProbes, pins: pi.pins]))
            }
        }

        assets("public")
    }
}

