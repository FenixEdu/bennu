define([ 'jquery', 'backbone', 'marionette', 'app', 'text!templates/SystemInfo.html' ],

function($, Backbone, Marionette, App, tpl) {
    return Backbone.Marionette.ItemView.extend({
        template : tpl,

        events : {
            'click .btn-error' : 'triggerError',
        },
        
        triggerError: function(e) {
            $.get('../api/bennu-core/system/error', null);  
        }
    });
});