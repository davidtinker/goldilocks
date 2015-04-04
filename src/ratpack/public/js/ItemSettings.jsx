var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var PiStore = require('./PiStore');

var ItemSettings = React.createClass({

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

    handleSubmit: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({
            type: 'update-item',
            id: {
                chartId: this.props.chart.id,
                id: this.props.item.id
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

    handleCancel: function(ev) {
        ev.preventDefault();
        if (this.props.onComplete) this.props.onComplete(false);
    },

    render: function() {
        var i = this.props.item || {};
        var tempProbes = [''].concat(this.state.tempProbes).map(function(p){
            return (<option value={p} key={p}>{p || 'None'}</option>)
        });
        var pins = [''].concat(this.state.pins).map(function(p){
            return (<option value={p} key={p}>{p || 'None'}</option>)
        });
        return (
            <form className="item-settings" onSubmit={this.handleSubmit}>
                <label>
                    <span>Name</span>
                    <input ref='name' defaultValue={i.name}/>
                </label>
                <label>
                    <span>Temp Probe</span>
                    <select ref='tempProbe' defaultValue={i.tempProbe || ''}>{tempProbes}</select>
                </label>
                <label>
                    <span>Output Pin</span>
                    <select ref='pin' defaultValue={i.pin || ''}>{pins}</select>
                </label>
                <label>
                    <span>Color</span>
                    <select ref='color' defaultValue={i.color || 'blue'}>
                        <option value='blue'>Blue</option>
                        <option value='orange'>Orange</option>
                    </select>
                </label>
                <div className='actions'>
                    <span/>
                    <span>
                        <input type='submit' value='Save'/>
                        <a className="btn cancel" href="" onClick={this.handleCancel}>Cancel</a>
                    </span>
                </div>
            </form>
        )
    }
});

module.exports = ItemSettings;