define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleHost.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',

        
    });
});