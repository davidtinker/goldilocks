var React = require('react');

var AppDispatcher = require('./AppDispatcher');

var AppSettings = React.createClass({

    handleSubmit: function(ev) {
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

    handleCancel: function(ev) {
        ev.preventDefault();
        if (this.props.onComplete) this.props.onComplete(false);
    },

    render: function() {
        return (
            <form className="app-settings" onSubmit={this.handleSubmit}>
                <label>
                    <span>Title</span>
                    <input ref='title' defaultValue={this.props.app.title}/>
                </label>
                <label>
                    <span>Temp Units</span>
                    <select ref='fahrenheit' defaultValue={this.props.app.fahrenheit}>
                        <option value='false'>Celsius</option>
                        <option value='true'>Fahrenheit</option>
                    </select>
                </label>
                <div className='actions'>
                    <span/>
                    <span>
                        <input type='submit' value='Save'/>
                        <a href="" className="btn cancel" onClick={this.handleCancel}>Cancel</a>
                    </span>
                </div>
            </form>
        )
    }
});

module.exports = AppSettings;