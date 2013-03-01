define([
    'backbone',
    'models/Host',
], function(Backbone, HostModel) {
	return Backbone.Collection.extend({

	    url: "/api/bennu-portal/hosts",

	    model: HostModel,
	    
	    parse: function(response){
	        return response.hosts;
	     },
	     
	     comparator: function (collection) {
	    	 return collection.get('hostname');
	     }

	});
});