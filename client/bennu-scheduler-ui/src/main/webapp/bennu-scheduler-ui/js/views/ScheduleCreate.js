define([
    'jquery',
    'backbone',
    'marionette',
    'text!templates/ScheduleCreate.html'
], function($, Backbone, Marionette,tpl) {
	

	return Backbone.Marionette.ItemView.extend({
		template: tpl,
		
		
		events : {
			"click #confirm" : "createSchedule",
		},
		
		createSchedule: function(e) {
			e.preventDefault();
	    	this.model.set({schedule : $("#schedule").val()});
	    	this.model.save(null,{ success : function () {
	    		Backbone.history.navigate("schedules", true);
	    	} });
		},
	
	});
});