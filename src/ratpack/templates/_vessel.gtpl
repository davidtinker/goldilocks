h2 v.name ?: 'New Vessel'

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
                        if (v.heater == option) args.checked = null
                        input(args)
                        span option.capitalize()
                    }
                }
                input(type: 'submit', value: 'Go')
            }
        }
    }
}
