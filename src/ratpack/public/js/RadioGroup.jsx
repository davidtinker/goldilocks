var React = require('react');

var RadioGroup = React.createClass({

    getInitialState: function() {
        return { value: this.value = this.props.value }
    },

    onChange: function(ev) {
        this.value = ev.target.value;
        console.log("change " + this.value);
        this.setState({value: this.value});
        if (this.props.onChange) this.props.onChange(this.value);
    },

    render: function() {
        var value = this.state.value;
        var name = this.props.name;
        var ops = this.props.options.map(function(o){ return (
            <label>
                <input type='radio' name={name} value={o.value} defaultChecked={value == o.value} autoFocus={!!o.autoFocus}/>
                <span>{o.label}</span>
            </label>
        )});
        return <div className="radio-group" onChange={this.onChange}>{ops}</div>
    }
});

module.exports = RadioGroup;

