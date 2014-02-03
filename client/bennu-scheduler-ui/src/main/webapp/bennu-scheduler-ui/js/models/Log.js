define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
		
		initialize: function(options){
			if (options) {
				this.taskName = options.taskName;
			}
		},
		
		baseUrl: "../api/bennu-scheduler/log/",
		
		url: function() {
			if (this.taskName && this.id) {
				return  this.baseUrl + this.taskName + "/" + this.id;
			}
		},
	
	defaults: {
		type : "Type",
		name : "Name",
    },
});

});