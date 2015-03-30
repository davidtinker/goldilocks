if (undefined === window['App']) window.App = {};

(function() {

    App.createTempChart = function(element, vesselId) {

        var margin = {top: 10, right: 20, bottom: 20, left: 40},
            width = 320 - margin.left - margin.right,
            height = 200 - margin.top - margin.bottom;

        var timeFormat = d3.time.format.multi([
            ["%M", function(d) { return d.getMinutes(); }],
            ["%I%p", function(d) { return true; }],
        ]);

        var x = d3.time.scale().range([0, width]);
        var y = d3.scale.linear().range([height, 0]).nice();
        var xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(10).tickSize(-height).tickFormat(timeFormat);
        var yAxis = d3.svg.axis().scale(y).orient("left").tickSize(-width);

        var probeLine = d3.svg.line()
            .interpolate("monotone")
            .x(function(d) { return x(d.date); })
            .y(function(d) { return y(d.temp); });

        var svg = d3.select(element).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        return {
            refresh: function () {
                d3.json("/rest/vessel/" + vesselId + "/history", function (error, data) {
                    var tempProbe = data.tempProbe;
                    var targetTemp = data.targetTemp;

                    svg.selectAll(".axis").remove();
                    svg.selectAll(".line").remove();

                    if (tempProbe.length == 0) return;

                    var e = d3.extent(tempProbe, function (d) { return d.date; });
                    if (e[1] - e[0] < 30 * 60 * 1000) e[0] = e[1] - 30 * 60 * 1000;
                    x.domain(e);

                    e = d3.extent(tempProbe, function (d) { return d.temp; });
                    if (targetTemp && targetTemp.length > 0) {
                        var e2 = d3.extent(targetTemp, function (d) { return d.temp; });
                        var min = d3.min(targetTemp, function (d) { return d.temp; });
                        if (min > 0.0) e[0] = Math.min(e[0], e2[0]);
                        e[1] = Math.max(e[1], d3.max(targetTemp, function (d) { return d.temp; }));
                    }
                    if (e[1] - e[0] < 5) {
                        var avg = (e[0] + e[1]) / 2;
                        e[0] = avg - 2.5;
                        e[1] = avg + 2.5;
                    }
                    y.domain(e);

                    svg.append("g")
                        .attr("class", "x axis")
                        .attr("transform", "translate(0," + height + ")")
                        .call(xAxis);

                    svg.append("g")
                        .attr("class", "y axis")
                        .call(yAxis);

                    if (targetTemp && targetTemp.length > 0) {
                        var targetLine = d3.svg.line()
                            .interpolate("step-before")
                            .x(function (d) { return x(d.date) })
                            .y(function (d) { return y(d.temp) });

                        // each time we get zero to non-zero transition generate a new target line
                        for (var i = 0; i < targetTemp.length; i++) {
                            if (targetTemp[i].temp > 0.0) {
                                var start = i;
                                for (; ++i < targetTemp.length && targetTemp[i].temp > 0.0;);
                                svg.append("path")
                                    .datum(targetTemp.slice(start, i))
                                    .attr("class", "line target-temp")
                                    .attr("d", targetLine);
                            }
                        }
                    }

                    svg.append("path")
                        .datum(tempProbe)
                        .attr("class", "line temp-probe")
                        .attr("d", probeLine);

                });
            }
        };
    };
})();
