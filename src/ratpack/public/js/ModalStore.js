var EventEmitter = require('events').EventEmitter;
var assign = require('object-assign');

var modals = [];

/**
 * Maintains a stack of modal components that are displayed above everything else.
 */
var ModalStore = assign({}, EventEmitter.prototype, {

    push: function(component) {
        modals.push(component);
        this.emit('change');
    },

    pop: function() {
        modals.pop();
        this.emit('change');
    },

    list: function() { return modals },

    addChangeListener: function(callback) { this.on('change', callback) },

    removeChangeListener: function(callback) { this.removeListener('change', callback) }
});

module.exports = ModalStore;