CoffeeManager.View.UserListView = Backbone.View.extend({

	el: $('#content'),

    render: function () {
        CoffeeManager.Util.renderTemplate("UserListView", this.el, { users: this.collection.toJSON() });
        return this;
    }
});