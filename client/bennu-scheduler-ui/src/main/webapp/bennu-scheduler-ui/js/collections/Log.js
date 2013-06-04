define([
    'backbone',
    'moment',
    'models/Log',
], function(Backbone, moment, LogModel) {
	return Backbone.Collection.extend({

	    url: "../api/bennu-scheduler/log",

	    model: LogModel,
	    
	    parse: function(response){
	        return response.logs;
	     },
	     
	     comparator: function (model) {
	    	 return -moment(model.get('start')).format("X");
	     }

	});
});