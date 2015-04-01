import tinker.goldilocks.Html

form(class: 'edit-item vert', 'data-item-id': c.id + '/' + i.id, method: 'post',
        action: '/chart/' + c.id + '/item/' + i.id, style: 'display:none') {
    label {
        span('Name')
        input(name: 'name', value: i.name)
    }
    label {
        span('Temp Probe')
        select(name: 'tempProbe') {
            def args = [value: ""]
            if (!i.tempProbe) args.selected = null
            option(value: "", 'None')
            tempProbes.each {
                args = [value: it]
                if (it == i.tempProbe) args.selected = null
                option(args, it)
            }
        }
    }
    label {
        span('Output Pin')
        select(name: 'pin') {
            def args = [value: ""]
            if (!i.pin) args.selected = null
            option(value: "", 'None')
            pins.each {
                args = [value: it]
                if (it == i.pin) args.selected = null
                option(args, it)
            }
        }
    }
    label {
        span('Colors')
        select(name: 'colorScheme') {
            ['orange', 'blue'].each {
                args = [value: it]
                if (it == i.colorScheme) args.selected = null
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
