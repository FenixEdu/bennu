define([
    'backbone',
    'models/App',
], function(Backbone, AppModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-dispatch/apps",

	    model: AppModel,
	    
	    parse: function(response){
	        return response.apps;
	     },
	    
	    comparator: function(app) {
	    	return app.get("path");
	    }

	});
});