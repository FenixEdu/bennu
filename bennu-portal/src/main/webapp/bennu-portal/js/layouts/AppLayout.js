define([
    'backbone',
    'marionette',
    'app',
    'text!templates/AppLayout.html',
    'views/Header',
    'views/Footer',
], function(Backbone, Marionette, App, tpl, HeaderView, FooterView) {

    return Backbone.Marionette.Layout.extend({

        template: tpl,

        regions: {
            headerRegion: "#header",
            contentRegion: "#content",
            footerRegion: "#footer"
        },

        onShow: function() {
                	App.layout.headerRegion.show(new HeaderView());
                    App.layout.footerRegion.show(new FooterView());
        }
    });
});
