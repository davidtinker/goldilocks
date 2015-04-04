var React = require('react');
var $ = require('jquery');

var AppDispatcher = require('./AppDispatcher');
var AppStore = require('./AppStore');

var TitleBar = require('./TitleBar.jsx');
var Chart = require('./Chart.jsx');

var Goldilocks = React.createClass({
    componentDidMount: function() {
        AppStore.addChangeListener(function(){
            this.setState(AppStore.getApp());
        }.bind(this));
        AppDispatcher.dispatch({type: 'refresh'});
        AppDispatcher.dispatch({type: 'refresh-pi'});
        //setInterval(function() { AppDispatcher.dispatch({type: 'refresh'}) }, 1000);
    },

    handleAddChart: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({type: 'addChart'});
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
                <a className="btn" href="" onClick={this.handleAddChart}>Add Chart</a>
            </div>
        )
    }
});

module.exports = Goldilocks;