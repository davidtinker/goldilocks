var React = require('react');

var ModalStore = require('./ModalStore');

var Clock = require('./Clock.jsx');
var Timer = require('./Timer.jsx');
var AppSettings = require('./AppSettings.jsx');

var TitleBar = React.createClass({

    onTitleClick: function(ev) {
        ModalStore.push(<AppSettings onComplete={ModalStore.pop.bind(ModalStore)} app={this.props.app}/>);
    },

    render: function() {
        return (
            <div className="title-bar">
                <div className="inner">
                    <Timer timerExpires={this.props.app.timerExpires}/>
                    <div className="brand-title" onClick={this.onTitleClick} title="Click to change settings">
                        <span className="brand">Goldilocks&deg;{this.props.app.fahrenheit ? 'F' : 'C'}</span>
                        <span className="title">{this.props.app.title}</span>
                    </div>
                </div>
                <Clock time={this.props.app.updated}/>
            </div>
        )
    }
});

module.exports = TitleBar;