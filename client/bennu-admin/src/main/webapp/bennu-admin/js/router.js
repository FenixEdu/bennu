define([
    'jquery',
    'underscore',
    'mustache',
    'backbone',
    'marionette',
    'client-factory',
    'app',
    'layouts/MenuLayout',
], function($, _, Mustache, Backbone, Marionette, MenuManager, App, MenuLayout) {

    var Router = Backbone.Marionette.AppRouter.extend({

        initialize: function() {
            console.log("initialize controller ...");
			if (!App.themes) {
				App.themes = new MenuManager.Collections.Theme();
    			App.themes.fetch();
			}
			if (!App.systemInfo) {
			    App.systemInfo = new MenuManager.Models.SystemInfo();
			    App.systemInfo.fetch({async:false});
			}
			
        },
        
        appRoutes: {
            "" : "portalConfig",
//    		"" : "showHosts",
//    		"hosts": "showHosts",
//    		"hosts/edit/:hostname" : "editHost",
//    		"hosts/create": "createHost",
    		"menu/:id" : "showMenu",
    		"system/info" : "systemInfo"
    	},
    	
    	controller: {
    		initialize : function() {
    		},

    		portalConfig : function() {
    		    new MenuManager.Models.PortalConfiguration().fetch({
    		        success : function(model) {
                        App.page.show(new MenuManager.Views.PortalConfiguration({
                            model : model
                        }));
    		        }
    		    });
    		},


    		//    		showHosts : function() {
//    			var hostCollection = new MenuManager.Collections.Host();
//    			hostCollection.fetch({
//    				success : function() {
//    					App.page.show(new MenuManager.Views.HostList({
//    						collection : hostCollection
//    					}));
//    				}
//    			});
//    		},
//    		
    		
    		
    		showMenu: function (id) {
    			var menuModel = new MenuManager.Models.Menu({id : id});
    			
    			App.menuLayout = new MenuLayout();
    			
				App.page.show(App.menuLayout);
				
    			menuModel.fetch({
    				success: function() {
    					menuModel.initialize();
    					var menuTree = new MenuManager.Views.SingleMenu({model : menuModel });	
    					App.menuLayout.tree.show(menuTree);
    					
    					if (!App.appsView) {
    						App.apps = new MenuManager.Collections.App();
    						App.apps.fetch();
    					}
    				},
    			});
    			
    		},
    		
    		createHost : function() {
    			App.page.show(new MenuManager.Views.HostCreate({model : new MenuManager.Models.Host()}));
    		},
    		
    		editHost : function(id) {
    			var hostModel = new MenuManager.Models.Host({id : id});
    			hostModel.fetch({
    				success: function() {
    					App.page.show(new MenuManager.Views.HostCreate({model : hostModel}));
    				}
    			});
    		},
    		
    		systemInfo : function() {
    		    App.page.show(new MenuManager.Views.SystemInfo({model: App.systemInfo }));
    		},
    	}
    });
    
    return Router;
    
});