var React = require('react');
var ModalStore = require('./ModalStore');

var TimerSettings = require('./TimerSettings.jsx');

var Timer = React.createClass({

    getInitialState: function() {
        var te = this.props.timerExpires;
        return { secondsLeft: te ? moment(te).diff(moment(), 'seconds') : null}
    },

    componentDidMount: function() {
        if (this.props.timerExpires) {
            this._interval = setInterval(function() { this.setState(this.getInitialState()) }.bind(this), 1000);
        }
    },

    componentWillUnmount: function() {
        clearInterval(this._interval);
    },

    onChangeSettings: function(ev) {
        ModalStore.push(
            <TimerSettings timer={this.state.timer} onComplete={ModalStore.pop.bind(ModalStore)}/>
        );
    },

    render: function() {
        if (this.state.secondsLeft == null) {
            return <div className="timer clickable" onClick={this.onChangeSettings}>0:00:00</div>
        } else {
            return (
            <div className="timer clickable" onClick={this.onChangeSettings}>
                {moment.unix(this.state.secondsLeft).format('HH:mm:ss')}
            </div>)
        }
    }
});

module.exports = Timer;
