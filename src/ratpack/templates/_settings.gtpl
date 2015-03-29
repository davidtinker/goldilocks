form(class: 'edit vert', method: 'post', action: '/settings', style: 'display:none') {
    label {
        span('Title')
        input(name: 'title', value: app.title)
    }
    label {
        span('Temp units')
        select(name: 'tempUnit') {
            ['Celsius', 'Fahrenheit'].each {
                args = [value: it.charAt(0)]
                if (args.value == 'C' && !app.fahrenheit || args.value == 'F' && app.fahrenheit) args.selected = null
                option(args, it)
            }
        }
    }
    div(class: 'actions') {
        span('')
        span {
            input(type: 'submit', value: 'Save')
        }
    }
}
