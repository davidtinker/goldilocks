var React = require('react');
var $ = require('jquery');

var Clock = require('./Clock.jsx');

var TitleBar = React.createClass({
    render: function() {
        return (
            <div className="title-bar">
                <h1>{this.props.data.title}</h1>
                <Clock data={this.props.data}/>
            </div>
        )
    }
});

module.exports = TitleBar;