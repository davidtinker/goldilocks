var $ = require('jquery');
var AppDispatcher = require('./AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var app = {};

var AppStore = assign({}, EventEmitter.prototype, {
    getApp: function() { return app },
    emitChange: function() { this.emit('change') },
    addChangeListener: function(callback) { this.on('change', callback) },
    removeChangeListener: function(callback) { this.removeListener('change', callback) }
});

function updateApp(data) {
    app = data;
    AppStore.emitChange();
}

AppDispatcher.register(function(action) {

    switch(action.type) {
        case 'refresh':
            $.getJSON('/rest', function(data){ updateApp(data); });
            break;

        case 'addChart':
            $.post('/rest/charts', function(data){ updateApp(data); });
            break;
    }
});

module.exports = AppStore;