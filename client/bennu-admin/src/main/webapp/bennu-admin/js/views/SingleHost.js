define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleHost.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        events: {
        	"click .add-menu" : "addMenu"
        },
        
        modelEvents: {
        	"change" : "render"
        },
        
        addMenu: function(e) {
        	var MenuManager = require("client-factory");
        	var menu = new MenuManager.Models.Menu();
        	var hostModel = this.model;
        	menu.save(null, {success : function(model) {
        		var menu = { id : model.id };
        		hostModel.set("menu", menu);
        		hostModel.save(null, function(e) {
        			Backbone.history.navigate("#menu/" + menu.id, true);
        		});
        	}});
        },
        
        
    });
});