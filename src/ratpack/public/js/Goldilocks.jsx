var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var AppStore = require('./AppStore');

var TitleBar = require('./TitleBar.jsx');
var Chart = require('./Chart.jsx');
var ModalStack = require('./ModalStack.jsx');

var Goldilocks = React.createClass({

    componentDidMount: function() {
        AppStore.addChangeListener(this._changeListener = function(){ this.setState(AppStore.getApp()); }.bind(this));
        AppDispatcher.dispatch({type: 'refresh'});
        AppDispatcher.dispatch({type: 'refresh-pi'});
        //this._interval = setInterval(function() { AppDispatcher.dispatch({type: 'refresh'}) }, 1000);
    },

    componentWillUnmount: function() {
        AppStore.removeChangeListener(this._changeListener);
        clearInterval(this._interval);
    },

    render: function() {
        if (!this.state) return (<div></div>);
        var chartNodes = this.state.charts.map(function(chart){
            return (<Chart chart={chart} key={chart.id}/>)
        });
        return (
            <div>
                <TitleBar app={this.state}/>
                {chartNodes}
                <ModalStack/>
            </div>
        )
    }
});

module.exports = Goldilocks;