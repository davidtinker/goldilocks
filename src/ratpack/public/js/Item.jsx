var React = require('react');
var TempReading = require('./TempReading.jsx');
var ItemSettings = require('./ItemSettings.jsx');

var Item = React.createClass({

    getInitialState: function() {
        return {
            showSettings: false,
            pinState: this.props.item.pinState || 'off'
        }
    },

    handleToggleSettings: function(ev) {
        this.setState({showSettings: !this.state.showSettings})
    },

    handleSettingsComplete: function() {
        this.setState({showSettings: false})
    },

    handleSubmit: function(ev) {
        ev.preventDefault();
    },

    handlePinStateChange: function(ev) {

    },

    render: function() {
        var i = this.props.item;
        var tp;
        if (i.tempProbe) tp = (
            <div onClick={this.handleToggleSettings} title="Click to configure">
                <TempReading name={i.name || 'Temp'} color={i.color} temp={i.temp}/>
            </div>
        );
        var ps = this.state.pinState;
        var pf;
        if (i.pin) pf = (
            <form onSubmit={this.handleSubmit}>
                <label onClick={this.handleToggleSettings} title="Click to configure">
                    {i.tempProbe ? 'Heater' : i.name || 'Pin'}
                </label>
                <span onChange={this.handlePinStateChange}>
                    <label><input type='radio' name='pinState' value='off' defaultChecked={ps == 'off'}/><span>Off</span></label>
                    <label><input type='radio' name='pinState' value='on' defaultChecked={ps == 'on'}/><span>On</span></label>
                    {i.tempProbe
                        ? <label><input type='radio' name='pinState' value='auto' defaultChecked={ps == 'auto'}/><span>Auto</span></label>
                        : ''}
                </span>
            </form>
        );
        return (
            <div className="item">
                {tp}
                {pf}
                {this.state.showSettings
                    ? <ItemSettings onComplete={this.handleSettingsComplete} item={i} chart={this.props.chart}/>
                    : ''}
            </div>
        )
    }
});

module.exports = Item;

//label(i.tempProbe ? 'Heater' : i.name)
//def ops = ['off', 'on']
//if (i.tempProbe) ops << 'auto'
//ops.each { option ->
//    label {
//    def args = [type: 'radio', name: 'pinState', value: option]
//    if (v.pinState == option || option == 'off' && !v.pinState) args.checked = null
//    input(args)
//    span(option.capitalize())
//}
//}
//if (i.tempProbe) {
//    label {
//        span('target ')
//        input(type: 'number', class: 'target-temp', name: 'targetTemp', value: Html.TEMP_FMT.format(v.targetTemp))
//    }
//}
//input(type: 'submit', value: 'Go')
