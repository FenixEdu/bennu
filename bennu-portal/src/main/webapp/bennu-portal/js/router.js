define([
    'jquery',
    'underscore',
    'mustache',
    'backbone',
    'marionette',
    'menu-manager',
    'app',
    'appLayout',
], function($, _, Mustache, Backbone, Marionette, MenuManager, App, AppLayout) {

    var Router = Backbone.Marionette.AppRouter.extend({

        initialize: function() {
            App.Layout = new AppLayout();
            App.Layout.render();
            App.page.show(App.Layout);
        },
        
        appRoutes: {
    		"" : "showHosts",
    		"hosts": "showHosts",
    		"hosts/edit/:hostname" : "editHost",
    		"hosts/create": "createHost",
    	},
    	
    	controller: {
    		initialize : function() {
    		},

    		showHosts : function() {
    			var hostCollection = new MenuManager.Collections.Host();
    			hostCollection.fetch({
    				success : function() {
    					App.Layout.contentRegion.show(new MenuManager.Views.HostList({
    						collection : hostCollection
    					}));
    				}
    			});
    		},

    		createHost : function() {
    			App.Layout.contentRegion.show(new MenuManager.Views.HostCreate({model : new MenuManager.Models.Host()}));
    		},
    		
    		editHost : function(id) {
    			var hostModel = new MenuManager.Models.Host({id : id});
    			hostModel.fetch({
    				success: function() {
    					App.Layout.contentRegion.show(new MenuManager.Views.HostCreate({model : hostModel}));
    				}
    			});
    		}
    	}
    });
    
    return Router;
    
});