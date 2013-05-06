define([
    'backbone',
    'models/Storage',
], function(Backbone, StorageModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-io/storage",

	    model: StorageModel,
	    
	    parse: function(response){
	        return response.storages;
	     },
	     
	     comparator: function (collection) {
	    	 return collection.get('name');
	     }

	});
});