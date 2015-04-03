var React = require('react');
var $ = require('jquery');

var Item = require('./Item.jsx');

var Chart = React.createClass({

    render: function() {
        var itemNodes = this.props.data.items.map(function(item) {
            return ( <Item data={item} key={item.id}/> )
        });
        return (
            <div className="chart" attr-id={this.props.data.id}>
                <h2>chart {this.props.data.id}</h2>
                {itemNodes}
            </div>
        )
    }
});

module.exports = Chart;