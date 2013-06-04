define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-scheduler/log",
	
	defaults: {
		type : "Type",
		name : "Name",
    },
});

});