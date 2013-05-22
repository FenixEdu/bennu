define([ 'jquery', 'backbone', 'marionette', 'app', 'text!templates/SystemInfo.html' ],

function($, Backbone, Marionette, App, tpl) {
    return Backbone.Marionette.ItemView.extend({
        template : tpl,

        events : {
        },
    });
});