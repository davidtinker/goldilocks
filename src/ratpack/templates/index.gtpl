import tinker.goldilocks.Html

html {
    head {
        meta(charset: 'utf-8')
        meta(name: 'viewport', content: 'width=device-width, initial-scale=1')
        title(app.title ?: 'Goldilocks')
        link(href: '/css/icons.css', rel: 'stylesheet')
        link(href: '/css/app.css', rel: 'stylesheet')
    }
    body {

        div(class: 'row') {
            div(class: 'col title') {
                h1(title: 'Click to edit settings') {
                    span(class: 'brand', app.title ?: 'Goldilocks')
                    span(class: 'time', id: 'time', Html.time(new Date()))
                }
                layout('_settings.gtpl', app: app)
            }
        }

        div(class: 'row') {
            app.charts.each { c ->
                layout('_chart.gtpl', c: c, tempProbes: tempProbes, pins: pins, app: app)
            }

            if (!app.charts) {
                div('No charts found. Click "Add Chart" to add a chart to get started.')
            }

            div(class: 'col main-actions') {
                form(method: 'post', action: '/chart') {
                    input(type: 'submit', value: 'Add Chart')
                }
            }
        }

        script(src: '/js/jquery-1.11.2.js', type: "text/javascript") { }
        script(src: '/js/d3.js', type: "text/javascript") { }
        script(src: '/js/tempchart.js', type: "text/javascript") { }
        script(src: '/js/app.js', type: "text/javascript") { }
    }
}