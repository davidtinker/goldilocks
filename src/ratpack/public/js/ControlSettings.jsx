var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var PiStore = require('./PiStore');
var TempGain = require('./TempGain.jsx');

var ControlSettings = React.createClass({

    getInitialState: function() {
        return PiStore.getPi();
    },

    componentDidMount: function() {
        PiStore.addChangeListener(this._changeListener = function(){ this.setState(PiStore.getPi()); }.bind(this));
        AppDispatcher.dispatch({type: 'refresh-pi'});
    },

    componentWillUnmount: function() {
        PiStore.removeChangeListener(this._changeListener);
    },

    onSubmit: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({
            type: 'update-control',
            id: {
                chartId: this.props.chart.id,
                id: this.props.control.id
            },
            data: {
                name: this.refs.name.getDOMNode().value.trim(),
                tempProbe: this.refs.tempProbe.getDOMNode().value.trim(),
                pin: this.refs.pin.getDOMNode().value.trim(),
                color: this.refs.color.getDOMNode().value.trim(),
                //gainPerMin: this.refs.gainPerMin.value,
                //lagPeriodSecs: this.refs.lagPeriodSecs.getDOMNode().value.trim(),
                autoTune: true
            }
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

    onDelete: function(ev) {
        ev.preventDefault();
        if (!window.confirm("Are you sure you want to delete this control?")) return;
        AppDispatcher.dispatch({
            type: 'delete-control',
            id: {
                chartId: this.props.chart.id,
                id: this.props.control.id
            }
        });
        if (this.props.onComplete) this.props.onComplete(true);
    },

    render: function() {
        var c = this.props.control || {};
        var tempProbes = [''].concat(this.state.tempProbes).map(function(p){
            return (<option value={p} key={p}>{p || 'None'}</option>)
        });
        var pins = [''].concat(this.state.pins).map(function(p){
            return (<option value={p} key={p}>{p || 'None'}</option>)
        });
        return (
            <form className="form control-settings" onSubmit={this.onSubmit} onKeyDown={this.onKeyDown}>
                <h1>Control Settings</h1>
                <label>
                    <span>Name</span>
                    <input ref='name' defaultValue={c.name} autoFocus="true"/>
                </label>
                <label>
                    <span>Temp Probe</span>
                    <select ref='tempProbe' defaultValue={c.tempProbe || ''}>{tempProbes}</select>
                </label>
                <label>
                    <span>Output Pin</span>
                    <select ref='pin' defaultValue={c.pin || ''}>{pins}</select>
                </label>
                <label>
                    <span>Color</span>
                    <select ref='color' defaultValue={c.color || 'blue'}>
                        <option value='blue'>Blue</option>
                        <option value='orange'>Orange</option>
                    </select>
                </label>
                <div className='actions'>
                    <input type='submit' value='Save'/>
                    <a key='cancel' className="btn cancel" onClick={this.onCancel}>Cancel</a>
                    <a key='delete' className="btn delete" onClick={this.onDelete}>Delete</a>
                </div>
            </form>
        )
    }
});

module.exports = ControlSettings;

/*
 <label>
 <span>Heater Gain</span>
 <TempGain ref='gainPerMin' value={c.gainPerMin} units=" / min"/>
 </label>
 <label>
 <span>Heater Lag</span>
 <span className="field">
 <input type="number" ref='lagPeriodSecs' defaultValue={c.lagPeriodSecs}/>
 <span> secs</span>
 </span>
 </label>

 */