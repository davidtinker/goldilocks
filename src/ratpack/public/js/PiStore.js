var $ = require('jquery');
var AppDispatcher = require('./AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var pi = {};

/**
 * Supplies info on the Raspberry Pi eg. the available temperature probes and GPIO pins.
 */
var PiStore = assign({}, EventEmitter.prototype, {
    getPi: function() { return pi },
    emitChange: function() { this.emit('change') },
    addChangeListener: function(callback) { this.on('change', callback) },
    removeChangeListener: function(callback) { this.removeListener('change', callback) }
});

function updatePi(data) {
    pi = data;
    PiStore.emitChange();
}

AppDispatcher.register(function(action) {
    switch(action.type) {
        case 'refresh-pi':
            $.getJSON('/rest/pi', updatePi);
            break;
    }
});

module.exports = PiStore;