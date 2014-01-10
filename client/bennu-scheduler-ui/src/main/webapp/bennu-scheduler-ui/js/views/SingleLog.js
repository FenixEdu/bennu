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
        
        serializeData: function() {
        	var data = this.model.toJSON();
        	if (data.javaCode) {
        		data.customLog = true;
        	} else {
        		data.customLog = false;
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