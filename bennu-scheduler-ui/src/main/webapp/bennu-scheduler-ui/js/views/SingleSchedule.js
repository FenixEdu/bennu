define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleSchedule.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        modelEvents: {
        	"change" : "render"
        },
        
        events: {
        	"click .delete-schedule" : "deleteSchedule"
        },
        
        deleteSchedule: function(e) {
        	var id = e.target.id;
        	var scheduleModel = this.model.collection.get(id);
        	scheduleModel.destroy({success : function() {
        		console.log("delete schedule " + id);
        	}});
        }
        
    });
});