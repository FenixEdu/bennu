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
            	"click .save-logging-storage" : "saveLoggingStorage",
            	"click #clear-schedules" : "clearAllSchedules",
            	"click #load-schedules" : "loadSchedules",
            	"change #input-load-dump-file" : "receiveSchedulesDump"
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
            
            clearAllSchedules: function(e) {
            	e.preventDefault();
            	var r = window.confirm("Are you sure you want to clear all schedules?");
            	
            	if (r == true) {
            		$.ajax(
                    		{ url : "../api/bennu-scheduler/schedule", 
                    		  type: 'DELETE', 
                    		  success : function() { 
                    			  			window.location.reload();
                    			  		}
                    		});
            	}
            },
            
            loadSchedules: function(e) {
            	e.preventDefault();
            	$("#input-load-dump-file").click();
            },
            
            receiveSchedulesDump: function(e) {
            	e.preventDefault();
            	var file = e.target.files[0];
            	var reader = new FileReader();
            	reader.onload = function() {
            		$.post("../api/bennu-scheduler/schedule/dump", 
            			   {data : reader.result}, 
            			   function(data) {
            				   window.location.reload();
            			   });
            	};
            	reader.readAsText(file);
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