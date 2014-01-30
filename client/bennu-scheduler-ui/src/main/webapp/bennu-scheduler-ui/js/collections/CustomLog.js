define([
    'moment',
    'collections/Log',
], function(moment, LogCollection) {
	return LogCollection.extend({

//		initialize : function(models, options) {
//			if(options) {
//				this.taskName = options.taskName;
//			}
//		},
//		
//		baseUrl: "../api/bennu-scheduler/custom/",
//		
//	    url: function() {
//	    	if (this.taskName) {
//	    		return this.baseUrl + this.taskName;
//	    	}
//	    	return this.baseUrl;
//	    },
//
//	    model: LogModel,
//	    
//	    parse: function(response){
//	        return response.logs;
//	     },
//	     
//	     comparator: function (model) {
//	    	 return -moment(model.get('start')).format("X");
//	     }
		
		baseUrl: "../api/bennu-scheduler/custom/"
		

	});
});