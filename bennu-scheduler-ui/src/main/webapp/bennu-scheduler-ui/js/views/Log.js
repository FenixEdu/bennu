define([
    'jquery',
    'backbone',
    'marionette',
    'moment',
    'app',
    'text!templates/Log.html'
], function($, Backbone, Marionette, moment, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        
        modelEvents: {
        	"change" : "render"
        },
        
        onClose: function() {
        	if (this.logging != 0) {
        		console.log("Clear " + this.logging);
        		clearInterval(this.logging);
        	}
        },
        
        refreshLog: function() {
        	var id = this.model.get("id");
        	$.get("../api/bennu-scheduler/log/cat/" + id, null, function(log) {
				$("#logs").html(log);
				});
        },
        
        initialize: function() {
        	var that = this;
        	this.logging = 0;
        	this.refreshLog();
        	if (this.model.get("end") == undefined) {
        		this.logging = setInterval(
        				function() {
        					that.refreshLog();
        				}, 3000);
        		console.log("setInterval: " + this.logging);
        	}
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