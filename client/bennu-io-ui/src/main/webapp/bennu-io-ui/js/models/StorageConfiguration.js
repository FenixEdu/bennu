define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-io/storage/config",
	
	defaults: {
		type : "Type",
		name : "Name",
    },
});

});