define([
    'jquery.ui',
    'backbone',
    'marionette',
    'views/SingleSchedule',
    'text!templates/ScheduleList.html'
], function(ui, Backbone, Marionette, SingleScheduleView,tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template:tpl,
    	itemView: SingleScheduleView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
        
            events: {
            	"click .save-logging-storage" : "saveLoggingStorage"
            },
            
            saveLoggingStorage: function(e) {
            	e.preventDefault();
            	var id = $("#loggingStorage").val();
            	$.ajax({
            		  type: "PUT",
            		  url: "../api/bennu-scheduler/config/" + id,
            		  data: null,
            		  success: function(data) {
            		  },
            		  dataType: "json"
            		});
            },
            
         onShow: function() {
        	$("#loggingStorage").append($("<option></option>").attr('value', 'null').text('-------'));
         	$.getJSON("../api/bennu-scheduler/config", null, function(data) {
         		$(data.availableStorages).each(function() {
         			var option = $("<option></option>");
         			$("#loggingStorage").append(option.attr('value', this.id).text(this.name));
         		});
         		$("#loggingStorage").val(data.loggingStorage.id);
         	});
         },
         
    });
});