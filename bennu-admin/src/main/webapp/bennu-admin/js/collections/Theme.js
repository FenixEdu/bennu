define([
    'backbone',
    'models/Theme',
], function(Backbone, ThemeModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-portal/themes",

	    model: ThemeModel,
	    
	    parse: function(response){
	        return response.themes;
	     },
	     
	    initialize: function () {
	    	
	    },

	});
});