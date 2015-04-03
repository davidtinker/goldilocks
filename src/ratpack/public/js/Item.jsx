var React = require('react');
var $ = require('jquery');

var Item = React.createClass({
    render: function() {
        return (<div className="item" attr-id={this.props.data.id}>item {this.props.data.id}</div>)
    }
});

module.exports = Item;