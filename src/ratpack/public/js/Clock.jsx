var React = require('react');
var $ = require('jquery');

var Clock = React.createClass({
    render: function() {
        return (<div className="clock">x{this.props.data.updated}</div>)
    }
});

module.exports = Clock;