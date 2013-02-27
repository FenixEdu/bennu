MenuManager.View.HomeView = Backbone.View.extend({

    el: $('#content'),

    render: function () {
    	MenuManager.Util.renderTemplate("HomeView", this.el);
        return this;
    }
});