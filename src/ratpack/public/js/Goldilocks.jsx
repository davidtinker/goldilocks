var React = require('react');
var $ = require('jquery');

var TitleBar = require('./TitleBar.jsx');
var Chart = require('./Chart.jsx');

var Goldilocks = React.createClass({
    getInitialState: function() {
        return {title: '', charts: []};
    },

    refreshState: function(){
        $.getJSON('/rest', function(app){
            this.setState(app);
        }.bind(this));
    },

    componentDidMount: function() {
        this.refreshState();
        setInterval(this.refreshState, 1000);
    },

    handleAddChart: function(ev) {
        ev.preventDefault();
        $.post('/rest/charts', function(app){
            this.setState(app);
        }.bind(this));
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