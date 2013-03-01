require(['jquery', 'jquery.bootstrap', 'backbone', 'mustache', 'marionette', 'app', 'router', 'menu-manager'], function($, jQueryBootstrap, Backbone, Mustache, Marionette, App, Router, MenuManager) {

    $.ajaxSetup({
        contentType: "application/json; charset=utf-8",
        statusCode : {
            401 : function() {
                // Redirect the to the login page.
                App.router.navigate("login", true);
            },
            403 : function() {
                // 403 -- Access denied
                App.router.navigate("login", true);
            },
            404 : function() {
                // 404 -- NOT FOUND
                App.router.navigate("login", true);
            },
			500 : function() {
                // 500 -- Access denied
                App.router.navigate("login", true);
            }
        }
    });

    Backbone.Marionette.Renderer.render = Mustache.to_html;

    App.addRegions({
        page: "body"
    });

    App.addInitializer(function() {
        Router.initialize();
        Backbone.history.start();
    });

    App.start();

});
