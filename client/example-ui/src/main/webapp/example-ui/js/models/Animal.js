define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
		defaults: {
			name : "John Doe",
			number : 0,
			location : "Everywhere"
	    },
	});
});