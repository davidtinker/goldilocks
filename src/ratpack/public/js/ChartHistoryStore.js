var $ = require('jquery');
var AppDispatcher = require('./AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var map = {};

/**
 * Supplies info on the Raspberry Pi eg. the available temperature probes and GPIO pins.
 */
var ChartHistoryStore = assign({}, EventEmitter.prototype, {
    get: function(chartId) { return map[chartId] },
    emitChange: function() { this.emit('change') },
    addChangeListener: function(callback) { this.on('change', callback) },
    removeChangeListener: function(callback) { this.removeListener('change', callback) }
});

AppDispatcher.register(function(action) {
    switch(action.type) {
        case 'refresh-chart-history':
            console.log("refresh-chart-history");
            $.getJSON('/rest/app/charts/' + action.id + "/history?minutes=" + (action.minutes || 60), function(data) {
                map[action.id] = data;
                ChartHistoryStore.emitChange();
            });
            break;
    }
});

module.exports = ChartHistoryStore;
