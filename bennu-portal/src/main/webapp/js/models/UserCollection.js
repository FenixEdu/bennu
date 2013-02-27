CoffeeManager.Collection.UserCollection = Backbone.Collection.extend({

    url: "api/users",

    model: CoffeeManager.Model.UserModel,
    
    parse: function(response) {
    	return response.users;
    }

});