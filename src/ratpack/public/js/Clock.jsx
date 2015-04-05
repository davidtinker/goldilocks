var React = require('react');
var moment = require('moment');

var Clock = React.createClass({
    render: function() {
        var t = moment(this.props.time).format('h:mm:ss A');
        return (<div className="clock">{t}</div>)
    }
});

module.exports = Clock;