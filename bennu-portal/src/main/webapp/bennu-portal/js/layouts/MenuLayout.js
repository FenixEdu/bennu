define([
    'jquery.ui',
    'backbone',
    'marionette',
    'text!templates/MenuLayout.html',
], function($, Backbone, Marionette, tpl) {

    return Backbone.Marionette.Layout.extend({

        template: tpl,
        
        regions: {
            tree: "#menu-tree",
            menu: "#menu",
        },
    });
});
