define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-scheduler/tasks",
	
	defaults: {
		type : "Type",
		name : "Name",
    },
});

});