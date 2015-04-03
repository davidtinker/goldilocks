var React = require('react');

var AppDispatcher = require('./AppDispatcher');

var AppSettings = React.createClass({

    handleSubmit: function(ev) {
        ev.preventDefault();
        var node = this.refs.title.getDOMNode();
        var title = node.value.trim();
        AppDispatcher.dispatch({
            type: 'update-settings',
            data: {title: title}
        });
        this.props.onSave();
    },

    render: function() {
        return (
            <form className="app-settings" onSubmit={this.handleSubmit}>
                <label>
                    <span>Title</span>
                    <input ref='title' defaultValue={this.props.app.title}/>
                </label>
                <div className='actions'>
                    <span/>
                    <span>
                        <input type='submit' value='Save'/>
                    </span>
                </div>
            </form>
        )
    }
});

module.exports = AppSettings;