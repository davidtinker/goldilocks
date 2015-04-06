var React = require('react');

var AppDispatcher = require('./AppDispatcher');

var AppSettings = React.createClass({

    onSubmit: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({
            type: 'update-settings',
            data: {
                title: this.refs.title.getDOMNode().value.trim(),
                fahrenheit: this.refs.fahrenheit.getDOMNode().value.trim() == "true"
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

    render: function() {
        return (
            <form className="app-settings" onSubmit={this.onSubmit} onKeyDown={this.onKeyDown}>
                <h1>Settings</h1>
                <label>
                    <span>Title</span>
                    <input ref='title' defaultValue={this.props.app.title} autoFocus='true'/>
                </label>
                <label>
                    <span>Temp Units</span>
                    <select ref='fahrenheit' defaultValue={this.props.app.fahrenheit}>
                        <option value='false'>Celsius</option>
                        <option value='true'>Fahrenheit</option>
                    </select>
                </label>
                <div className='actions'>
                    <input type='submit' value='Save'/>
                    <a href="" className="btn cancel" onClick={this.onCancel}>cancel</a>
                </div>
            </form>
        )
    }
});

module.exports = AppSettings;