MenuManager.View.FooterView = Backbone.View.extend({
	
	el: $('#footer'),

    render: function () {
    	MenuManager.Util.renderTemplate("FooterView", this.el);
		return this;	
    }

});