var React = require('react');

var AppDispatcher = require('./AppDispatcher');

var ChartSettings = React.createClass({

    onSubmit: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({
            type: 'update-chart',
            id: this.props.chart.id,
            data: { minutes: this.refs.minutes.getDOMNode().value.trim()}
        });
        if (this.props.onComplete) this.props.onComplete(true);
    },

    onAddControl: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({type: 'add-control', id: {chartId: this.props.chart.id}});
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
        if (!window.confirm("Are you sure you want to delete this chart?")) return;
        AppDispatcher.dispatch({
            type: 'delete-chart',
            id: this.props.chart.id
        });
        if (this.props.onComplete) this.props.onComplete(true);
    },

    render: function() {
        var c = this.props.chart;
        return (
            <form className="app-settings" onSubmit={this.onSubmit} onKeyDown={this.onKeyDown}>
                <h1>Chart Settings</h1>
                <label>
                    <span>Time Span</span>
                    <select ref='minutes' defaultValue={c.minutes} autoFocus='true'>
                        <option value='30'>30m</option>
                        <option value='60'>1h</option>
                        <option value='90'>1.5h</option>
                        <option value='120'>2h</option>
                        <option value='180'>3h</option>
                        <option value='240'>4h</option>
                        <option value='480'>8h</option>
                        <option value='1440'>24h</option>
                    </select>
                </label>
                <label>
                    <span>&nbsp;</span>
                    <button className="add" onClick={this.onAddControl}>Add Control</button>
                </label>
                <div className='actions'>
                    <input type='submit' value='Save'/>
                    <a key='cancel' className="btn cancel" onClick={this.onCancel}>Cancel</a>
                </div>
            </form>
        )
            //<a key='delete' className="btn delete" onClick={this.onDelete}>Delete</a>
    }
});

module.exports = ChartSettings;