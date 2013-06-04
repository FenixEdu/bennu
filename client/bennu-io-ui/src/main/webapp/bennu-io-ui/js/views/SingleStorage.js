define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleStorage.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        modelEvents: {
        	"change" : "render",
        	"destroy" : "render",
        },
        
        events: {
        	"click .btn-delete" : "deleteStorage",
        	"click .btn-convert" : "convertStorage"
        },
        
        deleteStorage: function(e) {
        	this.model.destroy({wait:true, success: function(model,response) {
        	}});
        },
        
        convertStorage : function(e) {
        	$.ajax({
        		  type: 'PUT',
        		  url: "../api/bennu-io/storage/convert/" + this.model.id,
        		  success: function(model, response) {
              				console.log("done!");
              			   },	
        		  dataType: 'json',
        		  async:false
        		});
        },
        
        serializeData: function(e) {
        	var model = this.model.toJSON();
        	model.canDelete = model.filesCount < 1;
        	return model;
        }
        
    });
});