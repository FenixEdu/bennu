define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleLog.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        modelEvents: {
        	"change" : "render"
        },
        
        events: {
        	"click #show-log-details" : "showLogDetails"
        },
        
        showLogDetails: function() {
        	var model = this.model;
        	require(['views/Log'], function(LogView) {
        		App.page.show(new LogView({model: model}));
        	});
        },
        
        serializeData: function() {
        	var data = this.model.toJSON();
        	
        	if (data.files) {
        		files = new Array();
            	$(data.files).each(function(i,e) {
            		var file = {};
            		file.url = "../api/bennu-scheduler/log/" + data.id + "/" + e;
            		file.name = e;
            		files.push(file);
            	});
            	data.files = files;
        	}
        	
        	$(data).each(function(i,e) {
        		e.start = moment(e.start).format("MMMM Do YYYY, h:mm:ss a");
        		if (e.end) {
        			e.end = moment(e.end).format("MMMM Do YYYY, h:mm:ss a");
        			e.finished = true;
        		} else {
        			e.end = "N/A";
        			e.finished = false;
        		}
        	});
        	
        	return data;
        }
        
    });
});