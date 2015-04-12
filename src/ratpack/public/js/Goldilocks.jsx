var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var AppStore = require('./AppStore');

var TitleBar = require('./TitleBar.jsx');
var Chart = require('./Chart.jsx');
var ModalStack = require('./ModalStack.jsx');

var Goldilocks = React.createClass({

    getInitialState: function() {
        return {app: { charts: [] }}
    },

    componentDidMount: function() {
        window.addEventListener("resize", this.updateDimensions);
        AppStore.addChangeListener(this._changeListener = function(){ this.setState({app: AppStore.getApp() })}.bind(this));
        AppDispatcher.dispatch({type: 'refresh'});
        AppDispatcher.dispatch({type: 'refresh-pi'});
        this._interval = setInterval(function() {
            AppDispatcher.dispatch({type: 'refresh'});
        }.bind(this), 10000);
    },

    componentWillUnmount: function() {
        AppStore.removeChangeListener(this._changeListener);
        clearInterval(this._interval);
        window.removeEventListener("resize", this.updateDimensions);
    },

    componentWillMount: function() {
        this.updateDimensions();
    },

    updateDimensions: function() {
        this.setState({width: window.width, height: window.height});
    },

    render: function() {
        if (!this.state) return (<div></div>);
        var chartNodes = this.state.app.charts.map(function(chart){
            return (<Chart chart={chart} key={chart.id}/>)
        });
        return (
            <div className="root">
                <TitleBar app={this.state.app}/>
                {chartNodes}
                <ModalStack/>
            </div>
        )
    }
});

module.exports = Goldilocks;