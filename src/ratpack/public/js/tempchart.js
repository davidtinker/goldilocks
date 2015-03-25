(function() {

    App.createTempChart = function(element, vesselId) {

        var margin = {top: 10, right: 10, bottom: 30, left: 30},
            width = 300 - margin.left - margin.right,
            height = 200 - margin.top - margin.bottom;

        var x = d3.time.scale().range([0, width]);
        var y = d3.scale.linear().range([height, 0]);
        var xAxis = d3.svg.axis().scale(x).orient("bottom");
        var yAxis = d3.svg.axis().scale(y).orient("left");

        var line = d3.svg.line()
            .interpolate("basis")
            .x(function(d) { return x(d.date); })
            .y(function(d) { return y(d.temp); });

        var svg = d3.select(element).append("svg")
            .attr("width", width + margin.left + margin.right)
            .attr("height", height + margin.top + margin.bottom)
            .append("g")
            .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

        d3.json("/rest/vessel/" + vesselId + "/history", function(error, data) {
            var tempProbe = data.tempProbe;
            var targetTemp = data.targetTemp;

            x.domain(d3.extent(tempProbe, function(d) { return d.date; }));

            var e = d3.extent(tempProbe, function (d) { return d.temp; });
            if (targetTemp) {
                var e2 = d3.extent(targetTemp, function (d) { return d.temp; });
                e[0] = Math.min(e[0], e2[0]);
                e[1] = Math.max(e[1], e2[1]);
            }
            y.domain(e);

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

            svg.append("g")
                .attr("class", "y axis")
                .call(yAxis)
                .append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 6)
                .attr("dy", ".71em")
                .style("text-anchor", "end")
                .text("C");

            svg.append("path")
                .datum(tempProbe)
                .attr("class", "line temp-probe")
                .attr("d", line);

            if (targetTemp) {
                svg.append("path")
                    .datum(targetTemp)
                    .attr("class", "line target-temp")
                    .attr("d", line);
            }
        });
    };
})();

$('.temp-chart').each(function(){
    var vid = $(this).attr('attr-id');
    App.createTempChart(this, vid)
});
