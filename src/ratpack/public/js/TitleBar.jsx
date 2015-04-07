var React = require('react');

var ModalStore = require('./ModalStore');

var Clock = require('./Clock.jsx');
var AppSettings = require('./AppSettings.jsx');

var TitleBar = React.createClass({

    onTitleClick: function(ev) {
        ModalStore.push(<AppSettings onComplete={ModalStore.pop.bind(ModalStore)} app={this.props.app}/>);
    },

    render: function() {
        return (
            <div className="title-bar">
                <Clock time={this.props.app.updated}/>
                <div className="clickable" onClick={this.onTitleClick} title="Click to change settings">
                    <span className="brand">Golidlocks&deg;{this.props.app.fahrenheit ? 'F' : 'C'}</span>
                    <span className="title">{this.props.app.title}</span>
                </div>
            </div>
        )
    }
});

module.exports = TitleBar;