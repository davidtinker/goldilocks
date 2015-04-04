var React = require('react');
var $ = require('jquery');

var Item = require('./Item.jsx');

var Chart = React.createClass({

    render: function() {
        var chart = this.props.chart;
        var itemNodes = chart.items.map(function(item) {
            return ( <Item item={item} chart={chart} key={item.id}/> )
        });
        return (
            <div className="chart">
                <h2>chart {chart.id}</h2>
                {itemNodes}
            </div>
        )
    }
});

module.exports = Chart;