require(['jquery', 'jquery.bootstrap', 'backbone', 'mustache', 'marionette', 'menu-manager','router', 'app'], 
		function($, jQueryBootstrap, Backbone, Mustache, Marionette, MenuManager, Router, App) {

    $.ajaxSetup({
        contentType: "application/json; charset=utf-8",
        statusCode : {
            401 : function() {
                // Redirect the to the login page.
                MenuManager.Router.navigate("login", true);
            },
            403 : function() {
                // 403 -- Access denied
            	MenuManager.Router.navigate("login", true);
            },
            404 : function() {
                // 404 -- NOT FOUND
            	MenuManager.Router.navigate("login", true);
            },
			500 : function() {
                // 500 -- Access denied
				MenuManager.Router.navigate("login", true);
            }
        }
    });

    Backbone.Marionette.Renderer.render = function(template, data){
    	if (data) {
    	  data['_mls'] = function() { 
				return function(val) { 
					if (this[val]) {
						if (this[val].pt) {
							return this[val].pt;
						}
					}
					return "";
				};
			};
    	}
		//console.log(data);
    	return Mustache.to_html(template, data);
    	};
    
    Backbone.emulateJSON = true;

    App.addRegions({
        page: "body"
    });

    App.addInitializer(function() {
    	MenuManager.Router = new Router();
        Backbone.history.start();
    });

    App.start();

});
