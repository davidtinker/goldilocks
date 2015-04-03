var React = require('react');

var Clock = require('./Clock.jsx');
var AppSettings = require('./AppSettings.jsx');

var TitleBar = React.createClass({

    getInitialState: function() { return {} },

    handleTitleClick: function(ev) {
        this.setState({showSettings: !this.state.showSettings})
    },

    handleSettingsSave: function(ev) {
        this.setState({showSettings: false})
    },

    render: function() {
        return (
            <div className="title-bar">
                <h1 onClick={this.handleTitleClick}>{this.props.app.title}</h1>
                <Clock data={this.props.app}/>
                {this.state.showSettings
                    ? <AppSettings onSave={this.handleSettingsSave} app={this.props.app}/>
                    : ''}
            </div>
        )
    }
});

module.exports = TitleBar;