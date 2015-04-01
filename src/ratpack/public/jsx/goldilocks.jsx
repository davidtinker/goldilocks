var Clock = React.createClass({
    render: function() {
        return (<div className="clock">{this.props.data.updated}</div>)
    }
});

var TitleBar = React.createClass({
    render: function() {
        return (
            <div className="title-bar">
                <h1>{this.props.data.title}</h1>
                <Clock data={this.props.data}/>
            </div>
        )
    }
});

var Item = React.createClass({
    render: function() {
        return (<div className="item" attr-id={this.props.data.id}>item {this.props.data.id}</div>)
    }
});

var Chart = React.createClass({

    render: function() {
        var itemNodes = this.props.data.items.map(function(item) {
            return ( <Item data={item} key={item.id}/> )
        });
        return (
            <div className="chart" attr-id={this.props.data.id}>
                <h2>chart {this.props.data.id}</h2>
                {itemNodes}
            </div>
        )
    }
});

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

React.render(
    <Goldilocks url="/rest"/>,
    document.getElementById('root')
);