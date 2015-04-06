var React = require('react');
var AppStore = require('./AppStore');
var ModalStore = require('./ModalStore');
var AppDispatcher = require('./AppDispatcher');

var TempReading = require('./TempReading.jsx');
var ControlSettings = require('./ControlSettings.jsx');
var PinState = require('./PinState.jsx');

var Control = React.createClass({

    onChangeSettings: function(ev) {
        ModalStore.push(
            <ControlSettings control={this.props.control} chart={this.props.chart} onComplete={ModalStore.pop.bind(ModalStore)}/>
        );
    },

    onChangePinState: function(ev) {
        ModalStore.push(
            <PinState control={this.props.control} chart={this.props.chart} onComplete={ModalStore.pop.bind(ModalStore)}/>
        );
    },

    render: function() {
        var i = this.props.control;
        var heater = i.tempProbe && i.pin;

        var tp;
        if (i.tempProbe) tp = (
            <div onClick={this.onChangeSettings} title="Click to configure">
                <TempReading name={i.name || 'Temp'} color={i.color} temp={i.temp}/>
            </div>
        );

        var tf;
        if (i.pin) {
            var ps = i.pinState || 'off';
            var psl = ps.charAt(0).toUpperCase() + ps.substring(1);
            tf = (
                <div onClick={this.onChangePinState} title="Click to change">
                    <label>{heater ? 'Heater' : i.name}</label>
                    <span className={"pin " + ps}>{psl}</span>
                    {heater && ps == 'auto'
                        ? <TempReading name={'Target'} color={i.color} temp={i.targetTemp}/>
                        : ''}
                </div>
            );
        }
        return (
            <div className="control">
                {!i.tempProbe && !i.pin
                    ? <div onClick={this.onChangeSettings}>Click to setup control</div>
                    : ''}
                {tp}
                {tf}
            </div>
        )
    }
});

module.exports = Control;
