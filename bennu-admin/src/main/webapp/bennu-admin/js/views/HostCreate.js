define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/HostCreate.html'
], function($, Backbone, Marionette, App, tpl) {
	

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
	    	
	    	this.model.set("theme", $("#theme").val());
	    	
	    	console.log(this.model.toJSON());
	    	
	    	this.model.save(null,{ success : function () {
	    		Backbone.history.navigate("hosts", true);
	    	} });
	    },
	    
	    serializeData: function(e) {
	    	var model = this.model.toJSON();
	    	model.themes = App.themes.toJSON();
	    	$(model.themes).each(function(i,t) {
	    		if (t.name === model.theme){
	    			t.selected = true;
	    		}
	    	});
	    	console.log("Themes : " + model);
	    	return model;
	    }
	});
});