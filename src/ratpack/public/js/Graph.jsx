var React = require('react');
var AppDispatcher = require('./AppDispatcher');

var Control = require('./Control.jsx');

var Graph = React.createClass({

    render: function() {
        var chart = this.props.chart;
        return (
            <div className="graph">
            </div>
        )
    }
});

module.exports = Graph;