MenuManager.View.CreateHostView = Backbone.View.extend({

	el: $('#content'),
	
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
	
	render: function() {
		MenuManager.Util.renderTemplate("CreateHostView", this.el, this.model.toJSON());
        return this;
	}

});