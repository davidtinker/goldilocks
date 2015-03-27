import java.text.SimpleDateFormat

html {
    head {
        meta(charset: 'utf-8')
        meta(name: 'viewport', content: 'width=device-width, initial-scale=1')
        title(app.title ?: 'Goldilocks')
        link(href: '/css/icons.css', rel: 'stylesheet')
        link(href: '/css/app.css', rel: 'stylesheet')
    }
    body {

        h1('Goldilocks ' + (app.title ?: ''))

        div(class: 'time', new SimpleDateFormat('HH:mm:ss').format(new Date()))

        app.vessels.each { v ->
            div(id: "v" + v.id, class: 'vessel') {
                layout('_vessel.gtpl', v: v, tempProbes: tempProbes, pins: pins, app: app)
            }
        }

        if (!app.vessels) {
            div('No vessels found. Click "Add Vessel" to add a hot liquor tank or mash tun to get started.')
        }

        div(class: 'actions') {
            form(method: 'post', action: '/vessel') {
                input(type: 'submit', value: 'Add Vessel')
            }
        }

        script(src: '/js/jquery-1.11.2.js', type: "text/javascript") { }
        script(src: '/js/d3.js', type: "text/javascript") { }
        script(src: '/js/app.js', type: "text/javascript") { }
        script(src: '/js/tempchart.js', type: "text/javascript") { }
    }
}