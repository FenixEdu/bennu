define([
    'backbone',
    'models/Schedule',
], function(Backbone, ScheduleModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-scheduler/schedule",

	    model: ScheduleModel,
	    
	    parse: function(response){
	        return response.schedule;
	     },
	     
	     comparator: function (collection) {
	    	 return collection.get('type');
	     }

	});
});