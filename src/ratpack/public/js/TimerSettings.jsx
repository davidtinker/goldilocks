var React = require('react');

var AppDispatcher = require('./AppDispatcher');

var TimerSettings = React.createClass({

    onSubmit: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({
            type: 'start-timer',
            data: { minutes: this.refs.minutes.getDOMNode().value.trim() }
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
        var ops = [];
        for (var i = 1; i < 60; i++) ops.push(<option value={i}>{i}</option>)
        for (; i <= 120; i += 5) ops.push(<option value={i}>{i}</option>)
        return (
            <form className="timer-settings" onSubmit={this.onSubmit} onKeyDown={this.onKeyDown}>
                <h1>Timer</h1>
                <label>
                    <span>Minutes</span>
                    <select ref='minutes' defaultValue={this.props.minutes}>{ops}</select>
                </label>
                <div className='actions'>
                    <input type='submit' value='Start Timer'/>
                    <a className="btn cancel" onClick={this.onCancel}>cancel</a>
                </div>
            </form>
        )
    }
});

module.exports = TimerSettings;