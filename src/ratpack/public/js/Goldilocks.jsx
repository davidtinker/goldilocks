var React = require('react');
var $ = require('jquery');

var AppDispatcher = require('./AppDispatcher');
var AppStore = require('./AppStore');

var TitleBar = require('./TitleBar.jsx');
var Chart = require('./Chart.jsx');

var Goldilocks = React.createClass({
    getInitialState: function() {
        return {title: '', charts: []};
    },

    componentDidMount: function() {
        AppStore.addChangeListener(function(){
            this.setState(AppStore.getApp());
        }.bind(this));
        var f = function() { AppDispatcher.dispatch({type: 'refresh'}) };
        f();
        setInterval(f, 1000);
    },

    handleAddChart: function(ev) {
        ev.preventDefault();
        AppDispatcher.dispatch({type: 'addChart'});
    },

    render: function() {
        var chartNodes = this.state.charts.map(function(chart){
            return (<Chart data={chart} key={chart.id}/>)
        });
        return (
            <div>
                <TitleBar data={this.state}/>
                {chartNodes}
                <a className="btn" href="" onClick={this.handleAddChart}>Add Chart</a>
            </div>
        )
    }
});

module.exports = Goldilocks;