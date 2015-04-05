var React = require('react');
var AppStore = require('./AppStore');
var AppDispatcher = require('./AppDispatcher');

var TempReading = require('./TempReading.jsx');
var ControlSettings = require('./ControlSettings.jsx');
var TempSpinner = require('./TempSpinner.jsx');

var Control = React.createClass({

    getInitialState: function() {
        return {
            showSettings: false,
            pinState: this.props.control.pinState || 'off'
        }
    },

    onToggleSettings: function(ev) {
        this.setState({showSettings: !this.state.showSettings})
    },

    onSettingsComplete: function() {
        this.setState({showSettings: false})
    },

    onSubmit: function(ev) {
        ev.preventDefault();
        var data = {pinState: this.state.pinState};
        if (this.refs.targetTemp) data.targetTemp = this.refs.targetTemp.value;
        AppDispatcher.dispatch({
            type: 'update-control',
            id: {
                chartId: this.props.chart.id,
                id: this.props.control.id
            },
            data: data
        });
    },

    onPinStateChange: function(ev) {
        if (ev.target.checked && ev.target.type == "radio") this.setState({pinState: ev.target.value});
    },

    render: function() {
        var i = this.props.control;
        var ps = this.state.pinState;

        var tp;
        if (i.tempProbe) tp = (
            <div onClick={this.onToggleSettings} title="Click to configure">
                <TempReading name={i.name || 'Temp'} color={i.color} temp={i.temp}/>
            </div>
        );

        var tf;
        if (i.tempProbe && i.pin) tf = (
            <span>
                <label><input type='radio' name='pinState' value='auto' defaultChecked={ps == 'auto'}/><span>Auto</span></label>
                <TempSpinner ref='targetTemp' label=' target ' value={i.targetTemp || 67.0}/>
            </span>
        );

        var pf;
        if (i.pin) pf = (
            <form onSubmit={this.onSubmit}>
                <label onClick={this.onToggleSettings} title="Click to configure">
                    {i.tempProbe ? 'Heater' : i.name || 'Pin'}
                </label>
                <span onChange={this.onPinStateChange}>
                    <label><input type='radio' name='pinState' value='off' defaultChecked={ps == 'off'}/><span>Off</span></label>
                    <label><input type='radio' name='pinState' value='on' defaultChecked={ps == 'on'}/><span>On</span></label>
                    {tf}
                </span>
                <input type="submit" value="Go"/>
            </form>
        );
        return (
            <div className="control">
                {tp}
                {pf}
                {this.state.showSettings
                    ? <ControlSettings onComplete={this.onSettingsComplete} control={i} chart={this.props.chart}/>
                    : ''}
            </div>
        )
    }
});

module.exports = Control;