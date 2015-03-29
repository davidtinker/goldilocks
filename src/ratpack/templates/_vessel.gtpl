import tinker.goldilocks.Html

h1(title: 'Click to edit') {
    span(v.name ?: 'New Vessel')
    span(class: 'temp-probe' + (v.tempError ? ' error' : ''), Html.temp(v.temp, app.fahrenheit))
}

if (!v.name || !v.tempProbe) {
    div(class: 'hint', '(click the title bar to configure this vessel)')
}

form(class: 'edit vert', method: 'post', action: '/vessel/' + v.id, style: 'display:none') {
    label {
        span('Name')
        input(name: 'name', value: v.name)
    }
    label {
        span('Temp Probe')
        select(name: 'tempProbe') {
            def args = [value: ""]
            if (!v.tempProbe) args.selected = null
            option(value: "", 'None')
            tempProbes.each {
                args = [value: it]
                if (it == v.tempProbe) args.selected = null
                option(args, it)
            }
        }
    }
    label {
        span('Heater Pin')
        select(name: 'heaterPin') {
            def args = [value: ""]
            if (!v.heaterPin) args.selected = null
            option(value: "", 'None')
            pins.each {
                args = [value: it]
                if (it == v.heaterPin) args.selected = null
                option(args, it)
            }
        }
    }
    label {
        span('Colors')
        select(name: 'colorScheme') {
            ['orange', 'blue'].each {
                args = [value: it]
                if (it == v.colorScheme) args.selected = null
                option(args, it)
            }
        }
    }
    div(class: 'actions') {
        span('')
        span {
            input(type: 'submit', name: 'action', value: 'Save')
            input(type: 'submit', name: 'action', value: 'Delete', class: 'delete')
        }
    }
}

div(class: 'temp-chart', "attr-id": v.id) { }

if (v.heaterPin) {
    form(class: 'heater', method: 'post', action: '/vessel/' + v.id) {
        label('Heater')
        ['off', 'on', 'auto'].each { option ->
            label {
                def args = [type: 'radio', name: 'heater', value: option]
                if (v.heater == option || option == 'off' && !v.heater) args.checked = null
                input(args)
                span(option.capitalize())
            }
        }
        label {
            span('target ')
            input(type: 'number', class: 'target-temp', name: 'targetTemp', value: Html.TEMP_FMT.format(v.targetTemp))
        }
        input(type: 'submit', value: 'Go')
    }
}
