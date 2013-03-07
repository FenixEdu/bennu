define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/MenuForm.html'
], function($, Backbone, Marionette, App, tpl) {
	

	return Backbone.Marionette.ItemView.extend({

		template: tpl,
		
		events : {
	    	"click #confirm" : "createMenu"
	    },
	    
	    createMenu : function(e) {
	    	var that = this;
	    	e.preventDefault();
	    	$('input').each(function(index, input) {
	    		if ($(input).attr('lang')) {
	    			var mls = that.model.get(input.name) || {};
	    			mls[$(input).attr('lang')] = $(input).val();
	    			that.model.set(input.name, mls);
	    		} else {
	    			that.model.set(input.name, $(input).val());
	    		}
	    	});
	    	
	    	console.log(this.model.toJSON());
	    	
	    	this.model.save(null,{ success : function () {
	    		$('.top-right').notify({
	    		    message: { text: 'Ok!' }
	    		  }).show();
	    	} });
	    },
	});
});