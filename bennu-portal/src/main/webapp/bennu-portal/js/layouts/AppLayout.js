define([
    'backbone',
    'marionette',
    'text!templates/AppLayout.html',
    'views/Header',
    'views/Footer',
], function(Backbone, Marionette, tpl, HeaderView, FooterView) {

    return Backbone.Marionette.Layout.extend({

        template: tpl,

        regions: {
            headerRegion: "#header",
            contentRegion: "#content",
            footerRegion: "#footer"
        },

        onShow: function() {
        	this.headerRegion.show(new HeaderView());
        	this.footerRegion.show(new FooterView());
        }
    });
});
