h2 {
    span(v.name ?: 'New Vessel')
    a(class: 'edit', href: '', 'Edit')
}

form(class: 'edit', method: 'post', action: '/vessel/' + v.id, style: 'display:none') {
    label('Name')
    input(name: 'name', value: v.name)
    label('Temp Probe')
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
    label('Heater Pin')
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
    input(type: 'submit', value: 'Save')
}

div(class: 'temp-chart', "attr-id": v.id) {
    layout('_tempchart.gtpl', v: v)
}

ul(class: 'temps') {
    li(class: 'current') {
        label('Current')
        span(v.temp)
        if (v.tempError) span(class: 'error', v.tempError)
    }
    if (v.heaterPin) {
        form(method: 'post', action: '/vessel/' + v.id) {
            li(class: 'target') {
                label('Target')
                span(v.targetTemp)
                input(type: 'number', name: 'targetTemp', value: v.targetTemp)
                label('Heater')
                ['off', 'auto', 'on'].each { option ->
                    label {
                        def args = [type: 'radio', name: 'heater', value: option]
                        if (v.heater == option || option == 'off' && !v.heater) args.checked = null
                        input(args)
                        span(option.capitalize())
                    }
                }
                input(type: 'submit', value: 'Go')
            }
        }
    }
}
