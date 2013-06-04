define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-scheduler/schedule",
	
	defaults: {
		name : "Title",
		type : "type",
		schedule : "* * * * *"
    },
	});

});