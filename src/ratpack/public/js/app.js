if (undefined === window['App']) window.App = {};

$('.title h1').click(function(ev){
    ev.preventDefault();
    $(ev.target).closest('.title').find('form.edit').toggle();
});

$('.vessel h1').click(function(ev){
    ev.preventDefault();
    $(ev.target).closest('.vessel').find('form.edit').toggle();
});

$('.vessel input.delete').click(function(ev){
    if (!window.confirm('Are you sure you want to delete this vessel?')) ev.preventDefault();
});

App.tempCharts = [];
$('.temp-chart').each(function(){
    var vid = $(this).attr('attr-id');
    var tc = App.createTempChart(this, vid);
    App.tempCharts.push(tc);
    tc.refresh();
});

setInterval(function(){
    $.getJSON('/refresh', function(data){
        $('#time').html(data.time);
        for (var i = 0; i < data.vessels.length; i++) {
            var v = data.vessels[i];
            var $v = $('#v' + v.id);
            var tp = $v.find('.temp-probe');
            if (tp.html() != v.temp) {
                tp.html(v.temp);
                App.tempCharts[i].refresh();
            }
        }
    });
}, 1000);

setInterval(function(){
    for (var i = 0; i < App.tempCharts.length; i++) App.tempCharts[i].refresh();
}, 30000);