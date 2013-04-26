define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleTask.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        modelEvents: {
        	"change" : "render"
        },
        
    });
});