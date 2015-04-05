var React = require('react');
var AppDispatcher = require('./AppDispatcher');

var Control = require('./Control.jsx');
var Graph = require('./Graph.jsx');

var Chart = React.createClass({

    onAddControl: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({type: 'add-control', id: {chartId: this.props.chart.id}});
    },

    render: function() {
        var chart = this.props.chart;
        var controlNodes = chart.controls.map(function(control) {
            return ( <Control control={control} chart={chart} key={control.id}/> )
        });
        return (
            <div className="chart">
                <h2>chart {chart.id}</h2>
                <Graph chart={chart}/>
                <div className="controls">
                    {controlNodes}
                </div>
                <a href="" className="btn" onClick={this.onAddControl}>Add Control</a>
            </div>
        )
    }
});

module.exports = Chart;