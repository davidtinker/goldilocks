import tinker.goldilocks.Html

div(id: "c" + c.id, class: 'col chart') {

    div(class: 'graph', "data-id": c.id) { }

    div(class: 'items') {
        c.items.each { i ->
            div(class: 'item', "data-id": i.id) {
                if (i.tempProbe || !i.tempProbe && !i.pin) {
                    span(class: 'item-temp' + i.colorScheme, title: 'Click to edit',
                        (i.name ?: 'Item') + ' ' + Html.temp(i.temp, app.fahrenheit))
                }
                layout('_edit_item.gtpl', c: c, i: i, tempProbes: tempProbes, pins: pins, app: app)

                if (i.pin) {
                    form(class: 'pin', method: 'post', action: '/chart/' + c.id + '/item/' + i.id) {
                        label(i.tempProbe ? 'Heater' : i.name)
                        def ops = ['off', 'on']
                        if (i.tempProbe) ops << 'auto'
                        ops.each { option ->
                            label {
                                def args = [type: 'radio', name: 'pinState', value: option]
                                if (v.pinState == option || option == 'off' && !v.pinState) args.checked = null
                                input(args)
                                span(option.capitalize())
                            }
                        }
                        if (i.tempProbe) {
                            label {
                                span('target ')
                                input(type: 'number', class: 'target-temp', name: 'targetTemp', value: Html.TEMP_FMT.format(v.targetTemp))
                            }
                        }
                        input(type: 'submit', value: 'Go')
                    }
                }
            }
        }
        div {
            form(method: 'post', action: '/chart/' + c.id + '/item') {
                input(type: 'submit', value: 'Add Item')
            }
        }
    }
}