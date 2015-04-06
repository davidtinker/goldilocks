var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var TempSpinner = require('./TempSpinner.jsx');
var RadioGroup = require('./RadioGroup.jsx');

var PinState = React.createClass({

    onSubmit: function(ev) {
        ev.preventDefault();
        var data = {pinState: this.refs.pin.value};
        if (this.refs.targetTemp) data.targetTemp = this.refs.targetTemp.value;
        AppDispatcher.dispatch({
            type: 'update-control',
            id: {
                chartId: this.props.chart.id,
                id: this.props.control.id
            },
            data: data
        });
        if (this.props.onComplete) this.props.onComplete(true);
    },

    onCancel: function(ev) {
        ev.preventDefault();
        if (this.props.onComplete) this.props.onComplete(false);
    },

    onKeyDown: function(ev) {
        if (ev.keyCode == 27) this.onCancel(ev);
    },

    render: function() {
        var c = this.props.control;
        var heater = c.tempProbe && c.pin;

        var ops = [
            {value: 'off', label: 'Off', autoFocus: true},
            {value: 'on', label: 'On'}
        ];
        if (heater) ops.push({value: 'auto', label: 'Auto'});

        return (
            <form className="pin-state" onSubmit={this.onSubmit} onKeyDown={this.onKeyDown}>
                <h1>{heater ? (c.name ? c.name + ' ' : '') + 'Heater' : (c.name ? c.name : 'Output Pin')}</h1>
                <RadioGroup ref='pin' name='pin' value={c.pinState || 'off'} options={ops}/>
                {heater ?
                    <label>
                        <span>Target</span>
                        <TempSpinner ref='targetTemp' label='Target ' value={c.targetTemp || 67.0}/>
                    </label>
                    : ''}
                <div className='actions'>
                    <input type='submit' value='Save'/>
                    <a href="" className="btn cancel" onClick={this.onCancel}>cancel</a>
                </div>
            </form>
        )
    }
});

module.exports = PinState;