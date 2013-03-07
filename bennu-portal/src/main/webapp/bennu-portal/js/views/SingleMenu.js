define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleMenu.html',
], function($, Backbone, Marionette, App, singleTpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template: singleTpl, 
    	tagName : "li",
    	
    	events: {
    		"click .edit-menu" : "editMenu",
    		"drop" : "drop",
    	},
    	
    	modelEvents: {
            'change': 'render'
            },
            
    	
    	drop: function(event, index) {
    		console.log("drop : " + index);
            this.$el.trigger('update-sort', [this.model, index]);
        },
    	
    	editMenu: function (e) {
    		var selectedMenuModel = this.model;
    		require(["menu-manager"], function(MenuManager) {
    			App.Layout.menuLayout.menu.show(new MenuManager.Views.MenuCreate({ model : selectedMenuModel}));
    		});
    		return false;
    	},
        
        initialize: function(){
            // grab the child collection from the parent model
            // so that we can render the collection as children
            // of this parent node
        	this.collection = this.model.menu;
        },
        
        appendHtml: function(cv, iv){
            // ensure we nest the child list inside of 
            // the current list item
        	cv.$("ul:first").append(iv.el);
        },
        
        onRender: function() {
            if(_.isUndefined(this.collection)){
                this.$("ul:first").remove();
            }
        }
        
    });
});