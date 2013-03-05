define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleMenu.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template: tpl,
    	tagName : 'ul',
    	
    	events: {
    		"click .edit-menu" : "editMenu",
    	},
    	
    	editMenu: function (e) {
    		require(["menu-manager"], function(MenuManager) {
    			var selectedMenuModel = new MenuManager.Models.Menu({id : e.target.id});
    			selectedMenuModel.fetch({
    				success: function() {
    					App.Layout.menuLayout.menu.show(new MenuManager.Views.MenuCreate({model : selectedMenuModel}));
    				}
    			});
    		});
    	},
        
        initialize: function(){
            // grab the child collection from the parent model
            // so that we can render the collection as children
            // of this parent node
            this.collection = this.model.menu;
        },
        
        appendHtml: function(collectionView, itemView){
            // ensure we nest the child list inside of 
            // the current list item
            collectionView.$("li:first").append(itemView.el);
        }
        
    });
});