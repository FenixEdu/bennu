define([
    'backbone',
    'models/StorageConfiguration',
], function(Backbone, StorageConfigurationModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-io/storage/config",

	    model: StorageConfigurationModel,
	    
	    parse: function(response){
	        return response.storageConfigurations;
	     },

	});
});