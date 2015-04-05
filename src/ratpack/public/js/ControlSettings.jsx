var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var PiStore = require('./PiStore');

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
                color: this.refs.color.getDOMNode().value.trim()
            }
        });
        if (this.props.onComplete) this.props.onComplete(true);
    },

    onCancel: function(ev) {
        ev.preventDefault();
        if (this.props.onComplete) this.props.onComplete(false);
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
            <form className="control-settings" onSubmit={this.onSubmit}>
                <label>
                    <span>Name</span>
                    <input ref='name' defaultValue={c.name}/>
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
                    <span/>
                    <span>
                        <input type='submit' value='Save'/>
                        <a key='cancel' className="btn cancel" href="" onClick={this.onCancel}>Cancel</a>
                        <a key='delete' className="btn delete" href="" onClick={this.onDelete}>Delete</a>
                    </span>
                </div>
            </form>
        )
    }
});

module.exports = ControlSettings;