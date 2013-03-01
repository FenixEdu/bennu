define([
    'jquery',
    'backbone',
    'marionette',
    'menu-manager',
    'text!templates/HostCreate.html'
], function($, Backbone, Marionette, MenuManager, tpl) {
	

	return Backbone.Marionette.ItemView.extend({

		template: tpl,
		
		events : {
	    	"click #confirm" : "createHost"
	    },
	    
	    createHost : function(e) {
	    	var that = this;
	    	e.preventDefault();
	    	$('input').each(function(index, input) {
	    		if ($(input).attr('lang')) {
	    			var mls = that.model.get(input.id) || {};
	    			mls[$(input).attr('lang')] = $(input).val();
	    			that.model.set(input.id, mls);
	    		} else {
	    			that.model.set(input.id, $(input).val());
	    		}
	    	});
	    	
	    	console.log(this.model.toJSON());
	    	
	    	this.model.save(null,{ success : function () {
	    		MenuManager.Application.navigate("hosts", true);
	    	} });
	    },
	});
});