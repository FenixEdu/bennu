define([
    'jquery',
    'underscore',
    'mustache',
    'backbone',
    'marionette',
    'menu-manager',
    'app',
    'appLayout',
    'layouts/MenuLayout',
], function($, _, Mustache, Backbone, Marionette, MenuManager, App, AppLayout, MenuLayout) {

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
    		"menu/:id" : "showMenu",
    		"menu/:id/:id" : "showMenu",
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
    		
    		
    		
    		showMenu: function (topMenuId, selectedMenuId) {
    			console.log("top: "  + topMenuId + " sel: " + selectedMenuId);
    			var menuModel = new MenuManager.Models.Menu({id : topMenuId});
    			
    			App.Layout.menuLayout = new MenuLayout();
    			App.Layout.menuLayout.menuId = topMenuId;
    			
				App.Layout.contentRegion.show(App.Layout.menuLayout);
				
    			menuModel.fetch({
    				success: function() {
    					var menu = menuModel.get("menu");
    					var menuCollection = new MenuManager.Collections.Menu(menu);
    					menuCollection.parent = menuModel;
    					menuTree = new MenuManager.Views.Menu({collection : menuCollection });
    					menuTree.menuId = topMenuId;
    					menuModel.unset("menu");
    					App.Layout.menuLayout.tree.show(menuTree);
    				},
    			});
    			
    			if (selectedMenuId) {
    				var selectedMenuModel = new MenuManager.Models.Menu({id : selectedMenuId});
    				selectedMenuModel.fetch({
    					success: function() {
    						App.Layout.menuLayout.menu.show(new MenuManager.Views.MenuCreate({model : selectedMenuModel}));
    					}
    				});
    			}
    			
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