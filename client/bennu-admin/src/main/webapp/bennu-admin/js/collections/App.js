define([
    'backbone',
    'models/App',
], function(Backbone, AppModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-portal/apps",

	    model: AppModel,
	    
	    parse: function(response){
	        return response.apps;
	     },
	    
	    comparator: function(app) {
	    	return app.get("path");
	    }

	});
});