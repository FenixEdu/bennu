define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'text!templates/SingleApp.html',
], function($, Backbone, Marionette, App, singleTpl) {

    return Backbone.Marionette.ItemView.extend({
    	template: singleTpl,
    	tagName : "li",
    	
    	events: {
    		"drop" : "drop",
    	},
    
    	drop : function(e, ui) {
    		console.log(e);
    		console.log(ui);
    	},
    	
    	onRender: function() {
    		require(["menu-manager"], function(MenuManager) {
    			MenuManager.Views.App.prototype.makeSortable();
            });
    	},
    	
    });
});