import java.text.SimpleDateFormat

html {
    head {
        meta(charset: 'utf-8')
        meta(name: 'viewport', content: 'width=device-width, initial-scale=1')
        title(app.title ?: 'Goldilocks')
        link(href: '/css/app.css', rel: 'stylesheet')
    }
    body {

        h1(app.title ?: 'Goldilocks')

        div(class: 'time', new SimpleDateFormat('HH:mm:ss').format(new Date()))

        app.vessels.each { v ->
            div(id: "v" + v.id, class: 'vessel') {
                layout('_vessel.gtpl', v: v, tempProbes: tempProbes, pins: pins)
            }
        }

        div(class: 'actions') {
            a(class: "setup", href: "/setup", 'Add Vessel')
        }

        script(src: '/js/jquery-1.11.2.js', type: "text/javascript") { }
        script(src: '/js/app.js', type: "text/javascript") { }
    }
}