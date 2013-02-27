CoffeeManager.View.UserView = Backbone.View.extend({

	el: $('#content'),

    render: function () {
        CoffeeManager.Util.renderTemplate("UserView", this.el, this.model.toJSON());
        return this;
    }
});