define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-portal/themes",
	
	defaults: {
		name : "Title",
    },
	});

});