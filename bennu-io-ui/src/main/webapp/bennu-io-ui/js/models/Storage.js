define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-io/storage",
	
	defaults: {
		name : "title",
		type : "type",
		filesCount : 0
    },
	});

});