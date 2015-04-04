var React = require('react');
var AppStore = require('./AppStore');

var TempReading = React.createClass({
    render: function() {
        var fahrenheit = AppStore.getApp().fahrenheit;
        var s = this.props.name + " ";
        var temp = this.props.temp;
        if (temp) {
            if (fahrenheit) temp = temp * 9 / 5 + 32;
            s += Math.round(temp * 10) / 10;
        } else {
            s += '?';
        }
        s += " \u00B0" + (fahrenheit ? 'F' : 'C');
        return (<span className={"temp " + this.props.color}>{s}</span>)
    }
});

module.exports = TempReading;