var React = require('react');
var ModalStore = require('./ModalStore');
var moment = require('moment');

var TimerSettings = require('./TimerSettings.jsx');

var Timer = React.createClass({

    componentDidMount: function() {
        //if (this.props.timerExpires) {
        //    this._interval = setInterval(function() { this.setState(this.getInitialState()) }.bind(this), 1000);
        //}
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
        if (this.props.timerExpires) {
            var secs = moment(this.props.timerExpires).diff(moment(), 'seconds');
            return (
                <div className="timer clickable" onClick={this.onChangeSettings}>
                    {moment.unix(secs).format('HH:mm:ss')}
                </div>)
        } else {
            return <div className="timer clickable" onClick={this.onChangeSettings}>0:00:00</div>
        }
    }
});

module.exports = Timer;
