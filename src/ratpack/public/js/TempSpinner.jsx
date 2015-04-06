var React = require('react');
var AppStore = require('./AppStore');

function options(min, max, prefix) {
    var ans = [];
    for (; min <= max; ++min) ans.push(<option value={min} key={min}>{prefix + min}</option>);
    return ans;
}

var TempSpinner = React.createClass({

    getInitialState: function() {
        return { value: this.value = this.props.value }
    },

    handleChange: function(ev) {
        var value = parseInt(this.refs.units.getDOMNode().value) + parseInt(this.refs.points.getDOMNode().value) / 10;
        if (AppStore.getApp().fahrenheit) value = (value - 32) * 5 / 9;
        this.value = value;
        this.setState({value: value});
        if (this.props.onChange) this.props.onChange(value);
    },

    render: function() {
        var value = this.state.value;
        var f = AppStore.getApp().fahrenheit;
        if (f) value = value * 9 / 5 + 32;

        var units = Math.floor(value);
        var points = Math.floor((value - units) * 10);

        return (
            <span onChange={this.handleChange}>
                <select ref='units' value={units}>{options(f ? 68 : 20, f ? 212 : 100, '')}</select>
                <select ref='points' value={points}>{options(0, 9, '.')}</select>
                <span>{'\u00B0' + (f ? 'F' : 'C')}</span>
            </span>
        );
    }
});

module.exports = TempSpinner;

