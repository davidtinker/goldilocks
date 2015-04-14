var React = require('react');
var d3 = require("d3");

var AppDispatcher = require('./AppDispatcher');
var AppStore = require("./AppStore");
var ChartHistoryStore = require("./ChartHistoryStore");
var ModalStore = require('./ModalStore');

var ChartSettings = require('./ChartSettings.jsx');

var Chart = React.createClass({

    getInitialState: function() {
        return { controls: ChartHistoryStore.get(this.props.chart.id) || [] }
    },

    onChartClick: function(ev) {
        ModalStore.push(
            <ChartSettings chart={this.props.chart} onComplete={ModalStore.pop.bind(ModalStore)}/>
        );
    },

    render: function() {
        return (<div className="temp-graph clickable" onClick={this.onChartClick}></div>);
    },

    componentDidMount: function() {
        this._changeListener = function(){
            this.setState({controls: ChartHistoryStore.get(this.props.chart.id) || []});
        }.bind(this);
        ChartHistoryStore.addChangeListener(this._changeListener);

        AppStore.addChangeListener(this._appChangeListener = function() {
            AppDispatcher.dispatch({
                type: 'refresh-chart-history',
                id: this.props.chart.id,
                minutes: this.props.chart.minutes
            });
        }.bind(this));
        this._appChangeListener();

        this.renderChart();
    },

    componentWillUnmount: function() {
        AppStore.removeChangeListener(this._appChangeListener);
        ChartHistoryStore.removeChangeListener(this._changeListener);
    },

    componentDidUpdate: function() {
        this.renderChart();
    },

    renderChart: function() {
        createChart(this.getDOMNode(), this.props, ChartHistoryStore.get(this.props.chart.id));
    }
});

module.exports = Chart;

var dateFn = function(d) { return d.date };
var tempFn = function(d) { return d.temp };

function widestExtent(lines, fn) {
    var e = d3.extent(lines[0].data, fn);
    for (i = 1; i < lines.length; i++) {
        var e2 = d3.extent(lines[i].data, fn);
        e[0] = Math.min(e[0], e2[0]);
        e[1] = Math.max(e[1], e2[1]);
    }
    return e;
}

function createChart(el, props, data) {
    d3.select(el).selectAll("svg").remove();
    if (!data) return;

    var fahrenheit = AppStore.getApp().fahrenheit;

    // flatten the line(s) for each control into a array of just lines + do c to f conversion if needed
    var lines = [], i, j, c;
    for (i = 0; i < data.length; i++) {
        c = data[i];
        if (c.tempProbe && c.tempProbe.length > 0) {
            lines.push({type: 'tempProbe', color: c.color, data: fixTemp(c.tempProbe, fahrenheit) });
        }
        if (c.targetTemp && c.targetTemp.length > 0) {
            lines.push({type: 'targetTemp', color: c.color + "-alt", data: fixTemp(c.targetTemp, fahrenheit) })
        }
    }

    var margin = {top: 10, right: 0, bottom: 40, left: 60},
        width = el.offsetWidth - margin.left - margin.right,
        height = el.offsetHeight - margin.top - margin.bottom;

    var timeFormat = d3.time.format.multi([
        ["%M", function(d) { return d.getMinutes(); }],
        ["%I%p", function(d) { return true; }]
    ]);

    var x = d3.time.scale().range([0, width]);
    var y = d3.scale.linear().range([height, 0]).nice();
    var xAxis = d3.svg.axis().scale(x).orient("bottom").tickPadding(9).ticks(10).tickSize(-height).tickFormat(timeFormat);
    var yAxis = d3.svg.axis().scale(y).orient("left").tickPadding(6).tickSize(-width);
    var y2Axis = d3.svg.axis().scale(y).orient("right").tickFormat('');

    var svg = d3.select(el).append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

    var mins = props.chart.minutes || 60;

    var e = lines.length > 0 ? widestExtent(lines, dateFn) : [0, new Date().time];
    if (!e[0] || e[1] - e[0] < mins * 60 * 1000) e[0] = e[1] - mins * 60 * 1000;
    x.domain(e);

    e = lines.length > 0 ? widestExtent(lines, tempFn) : [67, 67];
    e[0] = Math.floor(e[0] - 2);
    e[1] = Math.floor(e[1] + 2 + 0.5);
    y.domain(e);

    var xfn = function(d) { return x(d.date) };
    var yfn = function(d) { return y(d.temp) };
    var probeLine = d3.svg.line().interpolate("basis").x(xfn).y(yfn);
    var targetLine = d3.svg.line().interpolate("step-before").x(xfn).y(yfn);

    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);

    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis);

    svg.append("g")
        .attr("class", "y2 axis")
        .attr("transform", "translate(" + width + " ,0)")
        .call(y2Axis);

    for (i = 0; i < lines.length; i++) {
        var line = lines[i];
        if (line.type == "targetTemp") {
            var d = line.data;
            // each time we get zero to non-zero transition generate a new target line
            for (j = 0; j < d.length; j++) {
                if (d[j].temp > 0.0) {
                    var start = j;
                    for (; ++j < d.length && d[j].temp > 0.0;);
                    svg.append("path")
                        .datum(d.slice(start, j))
                        .attr("class", "line " + line.color)
                        .attr("d", targetLine);
                }
            }
        } else {
            svg.append("path")
                .datum(line.data)
                .attr("class", "line " + line.color)
                .attr("d", probeLine);
        }
    }
}

function fixTemp(a, fahrenheit) {
    if (!fahrenheit) return a;
    var ans = [];
    for (var i = 0; i < a.length; i++) {
        var e = a[i];
        ans.push({date: e.date, temp: e.temp * 9 / 5 + 32})
    }
    return ans;
}