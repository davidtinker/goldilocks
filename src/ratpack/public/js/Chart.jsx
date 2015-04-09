var React = require('react');
var AppDispatcher = require('./AppDispatcher');

var ModalStore = require('./ModalStore');
var Control = require('./Control.jsx');
var TempGraph = require('./TempGraph.jsx');
var ChartSettings = require('./ChartSettings.jsx');

var Chart = React.createClass({

    onChangeSettings: function(ev) {
        ModalStore.push(
            <ChartSettings chart={this.props.chart} onComplete={ModalStore.pop.bind(ModalStore)}/>
        );
    },

    render: function() {
        var chart = this.props.chart;
        var controlNodes = chart.controls.map(function(control) {
            return ( <Control control={control} chart={chart} key={control.id}/> )
        });
        return (
            <div className="chart">
                <div className="inner">
                    <TempGraph chart={chart} onClick={this.onChangeSettings}/>
                    <div className="controls">{controlNodes}</div>
                </div>
            </div>
        )
    }
});

module.exports = Chart;