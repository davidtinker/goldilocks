var React = require('react');

var AppDispatcher = require('./AppDispatcher');
var AppStore = require('./AppStore');
var ModalStore = require('./ModalStore');

var Clock = require('./Clock.jsx');
var Chart = require('./Chart.jsx');
var Control = require('./Control.jsx');
var AppSettings = require('./AppSettings.jsx');
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

    onTitleClick: function(ev) {
        ModalStore.push(<AppSettings onComplete={ModalStore.pop.bind(ModalStore)} app={this.state.app}/>);
    },

    render: function() {
        if (!this.state) return (<div></div>);
        var app = this.state.app;

        var charts = app.charts.map(function(chart){
            var controls = chart.controls.map(function(control) {
                return <Control control={control} chart={chart} key={control.id}/>
            });
            return (
                <tr key={chart.id}>
                    <td colSpan="2" className="chart"><Chart chart={chart}/></td>
                    <td>{controls}</td>
                </tr>
            )
        });

        return (
            <div className="root">
                <table>
                    <tbody>
                    <tr>
                        <td className="header clickable" onClick={this.onTitleClick}>
                            <div className={"brand" + (app.title ? "" : " brand-no-title")}>Goldilocks&deg;{app.fahrenheit ? 'F' : 'C'}</div>
                            <div className="title">{app.title}</div>
                        </td>
                        <td></td>
                        <td><Clock time={app.updated}/></td>
                    </tr>
                    {charts}
                    </tbody>
                </table>
                <ModalStack/>
            </div>
        )
    }
});

module.exports = Goldilocks;