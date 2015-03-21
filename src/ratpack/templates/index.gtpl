html {
    head {
        meta charset: 'utf-8'
        meta name: 'viewport', content: 'width=device-width, initial-scale=1'
        title app.title ?: 'Goldilocks'
        link href: 'css/app.css', rel: 'stylesheet'
    }
    body {
        h1 app.title ?: 'Goldilocks'

        app.vessels.each { v ->
            div(id: "v" + v.id, class: 'vessel') {
                layout('_vessel.gtpl', v: v)
            }
        }



        script src: 'js/app.js'
    }
}