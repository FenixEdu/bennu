define([
    'jquery',
    'underscore',
    'marionette',
    'text!templates/Header.html',
], function($, _, Marionette, tpl) {

    return Backbone.Marionette.CompositeView.extend({

        template: tpl,

        onShow: function() {
            $('.dropdown-toggle').dropdown();
        },

        selectMenuItem : function(menuItem) {
            $('.nav li').removeClass('active');
            if (menuItem) {
                $('.' + menuItem).addClass('active');
            }
        }
    });
});