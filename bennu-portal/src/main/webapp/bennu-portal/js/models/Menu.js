define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
		urlRoot : "/api/bennu-portal/menu",
		
		defaults: {
	    },
	    
	    initialize: function(){
	        var menu = this.get("menu");
	        if (menu){
	        	console.log("init menu model:" + menu);
	        	var MenuCollection = require("collections/Menu");
	        	this.menu = new MenuCollection(menu);
	            this.unset("menu");
	        }
	    }
    
	});

});