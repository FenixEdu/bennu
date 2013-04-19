define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleMenu.html',
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template: tpl, 
    	tagName : "li",
    	attributes: function() {
    		return {
    			class : "toggler" + ((this.hasMenu()) ? " plus" : ""),
    			};
    	},
    	
    	events: {
    		"click .edit-menu" : "editMenu",
    		"drop" : "drop",
    		"click .show-menu" : "showMenu",
    		"click .toggler" : "toggleMenu",
    	},
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render',
            },
            
    	toggleMenu: function(e) {
    		var uls = $("ul", e.target);
    		uls.toggle();
    		if($(uls).is(":visible")) {
    		    $(e.target).removeClass("plus");
    		    $(e.target).addClass("minus");
    		  } else {
    		    $(e.target).removeClass("minus");
    		    $(e.target).addClass("plus");
    		  }
    	},
    	
        drop: function(e, i) {
        	var collection = this.model.collection;
        	var siblingsandme =$(this.el).parent().children();
        	var ids = siblingsandme.map(function (i,e) { return $(e).children("a").attr("id"); });
        	var changed = false;
        	ids.each(function (i,id) {
        		var model = collection.get(id);
        		var order = i + 1;
        		var modelOrder = model.get("order");
        		if (modelOrder != order) {
        			if (!changed) {
        				changed = true;
        			}
        			model.set({order : order});
        			//console.log("prev: " + modelOrder + " new: " + order);
        		}
        	});
        	
        	collection.sort();
        	var parentMenu = collection.parent;
        	//console.log("before set ");
        	//console.log(parentMenu.toJSON());
        	parentMenu.set({menu : collection.toJSON()});
        	//console.log("after set ");
        	//console.log(parentMenu.toJSON());
        	if (changed) {
        		parentMenu.save(null,
            		{ success : function() {
            						//console.log("parent was saved!");
            					},
            		  error:	function() {
            			  			//console.log("error!");
            		  }
            		});
        	} else {
        		//console.log("nothing changed, don't save");
        	}
        	return false;
        },
        
    	
    	editMenu: function (e) {
    		var selectedMenuModel = this.model;
    		require(["menu-manager"], function(MenuManager) {
    			App.menuLayout.menu.show(new MenuManager.Views.MenuCreate({ model : selectedMenuModel}));
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
        },
        
        onShow: function() {
        	//$("#menu-tree > li > ul > li > ul").hide();
        	$(".toggler .plus ul").hide();
        },
        
        hasMenu: function() {
        	if (this.collection) {
        		return this.collection.length > 0;
        	}
        	if (this.model.menu) {
        		return this.model.menu.length > 0;
        	}
        },
        
        serializeData: function() {
        	var model = this.model.toJSON();
        	model.hasMenu = this.hasMenu();
        	return model;
        },
        
        showMenu: function(e) {
        	Backbone.history.navigate("#menu/" + e.target.id, true);
        }
        
        
        
    });
});