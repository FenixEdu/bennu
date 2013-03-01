define([
    'backbone',
    'marionette',
    'text!templates/Footer.html'
], function(Backbone, Marionette, tpl) {

    return Backbone.Marionette.ItemView.extend({

        el: tpl

    });
});