var React = require('react');
var moment = require('moment');

var Clock = React.createClass({

    getInitialState: function() {
        return { time: moment(this.props.time) }
    },

    componentDidMount: function() {
        this._interval = setInterval(function() {
            this.setState({time: this.state.time.clone().add(1, 'seconds')});
        }.bind(this), 1000);
    },

    componentWillUnmount: function() {
        clearInterval(this._interval);
    },

    render: function() {
        var t = this.state.time.format('h:mm:ss');
        return (<div className="clock">{t}</div>)
    }
});

module.exports = Clock;