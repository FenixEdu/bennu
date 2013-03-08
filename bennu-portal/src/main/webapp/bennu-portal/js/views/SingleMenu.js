define([
    'jquery.ui',
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
            
    	
        drop: function(e, i) {
        	var collection = this.model.collection;
        	var siblingsandme =$(this.el).parent().children();
        	var ids = siblingsandme.map(function (i,e) { return $(e).children("a").attr("id"); });
        	
        	ids.each(function (i,id) {
        		var model = collection.get(id);
        		var order = i + 1;
        		var modelOrder = model.get("order");
        		if (modelOrder != order) {
        			model.set({order : order});
        			console.log("prev: " + modelOrder + " new: " + order);
        		}
        	});
        	
        	collection.sort();
        	var parentMenu = collection.parent;
        	console.log("before set ");
        	console.log(parentMenu.toJSON());
        	parentMenu.set({menu : collection.toJSON()});
        	console.log("after set ");
        	console.log(parentMenu.toJSON());
        	
            parentMenu.save(null,
            		{ success : function() {
            						console.log("parent was saved!");
            					},
            		  error:	function() {
            			  			console.log("error!");
            		  }
            		});
            
        	return false;
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
            require(["menu-manager"], function(MenuManager) {
            	MenuManager.Views.Menu.prototype.makeSortable();
            });
        }
        
    });
});