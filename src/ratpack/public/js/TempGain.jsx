var React = require('react');
var AppStore = require('./AppStore');

var TempGain = React.createClass({

    getInitialState: function() {
        return { value: this.value = this.props.value }
    },

    handleChange: function(ev) {
        var value = parseFloat(this.refs.temp.getDOMNode().value);
        if (AppStore.getApp().fahrenheit) value = (value - 32) * 5 / 9;
        this.value = value;
        this.setState({value: value});
        if (this.props.onChange) this.props.onChange(value);
    },

    render: function() {
        var value = this.state.value;
        var f = AppStore.getApp().fahrenheit;
        if (f) value = value * 9 / 5 + 32;

        return (
            <span onChange={this.handleChange} className="input temp-input">
                <input ref='temp' defaultValue={value}/>
                <span>{' \u00B0' + (f ? 'F' : 'C')}</span>
                <span>{this.props.units}</span>
            </span>
        );
    }
});

module.exports = TempGain;

