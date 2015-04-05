var React = require('react');

var ModalStore = require('./ModalStore');

var ModalStack = React.createClass({

    getInitialState: function() { return { modals: ModalStore.list()} },

    componentDidMount: function() {
        ModalStore.addChangeListener(this._changeListener = function(){ this.setState(ModalStore.list()); }.bind(this));
    },

    componentWillUnmount: function() {
        ModalStore.removeChangeListener(this._changeListener);
    },

    render: function() {
        var modalNodes = this.state.modals.map(function(c, i) { return (
            <div key={i} className="modal">{c}</div>
        )});
        return (<div>{modalNodes}</div>)
    }
});

module.exports = ModalStack;