define([
    'backbone',
    'moment',
    'models/Log',
], function(Backbone, moment, LogModel) {
	return Backbone.Collection.extend({
		
		initialize : function(models, options) {
			if(options) {
				this.taskName = options.taskName;
			}
		},
		
		baseUrl: "../api/bennu-scheduler/log/",
		
	    url: function() {
	    	if (this.taskName) {
	    		return  this.baseUrl + this.taskName;
	    	}
	    	return this.baseUrl;
	    },

	    model: LogModel,
	    
	    parse: function(response){
	        var logs = response.logs;
	        if (this.taskName === undefined) {
	        	$(logs).each(function() { this.showDetails = true;});
	        }
	        return logs;
	     },
	     
	     comparator: function (model) {
	    	 return -moment(model.get('start')).format("X");
	     }

	});
});