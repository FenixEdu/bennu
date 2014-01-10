define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
		urlRoot : "../api/bennu-portal/menu",
		
		defaults: {
	    },
	    
	    initialize: function(){
	        var menu = this.get("menu");
	        if (menu){
	        	var MenuCollection = require("collections/Menu");
	        	this.menu = new MenuCollection(menu);
	        	this.menu.parent = this;
	            this.unset("menu");
	        }
	    },
	    
	});

});