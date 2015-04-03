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

function PUT(url, data) {
    var args = { type: "PUT", url: url, contentType: "application/json"};
    if (data) args.data = JSON.stringify(data);
    return $.ajax(args);
}

AppDispatcher.register(function(action) {

    switch(action.type) {
        case 'refresh':
            $.getJSON('/rest', updateApp);
            break;

        case 'add-chart':
            $.post('/rest/charts', updateApp);
            break;

        case 'update-settings':
            PUT('/rest', action.data).success(updateApp);
            break;
    }
});

module.exports = AppStore;