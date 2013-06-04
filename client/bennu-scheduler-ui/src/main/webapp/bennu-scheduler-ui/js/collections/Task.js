define([
    'backbone',
    'models/Task',
], function(Backbone, TaskModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-scheduler/tasks",

	    model: TaskModel,
	    
	    parse: function(response){
	        return response.tasks;
	     },

	});
});