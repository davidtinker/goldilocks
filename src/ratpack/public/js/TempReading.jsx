var React = require('react');
var AppStore = require('./AppStore');

var TempReading = React.createClass({
    render: function() {
        var fahrenheit = AppStore.getApp().fahrenheit;
        var temp = this.props.temp;
        var s;
        if (temp) {
            if (fahrenheit) temp = temp * 9 / 5 + 32;
            s = '' + Math.round(temp * 10) / 10;
            if (s.indexOf('.') < 0) s += '.0';
        } else {
            s = '?';
        }
        if (this.props.showUnits) s += " \u00B0" + (fahrenheit ? 'F' : 'C');
        return (
            <div className={"temp " + this.props.color}>
                <label>{this.props.name}</label>
                <span className="value">{s}</span>
            </div>
        )
    }
});

module.exports = TempReading;