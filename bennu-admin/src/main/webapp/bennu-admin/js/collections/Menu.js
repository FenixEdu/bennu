define([
    'backbone',
    'models/Menu',
], function(Backbone, MenuModel) {
	return Backbone.Collection.extend({

	    model: MenuModel,
	    
	    comparator: function(menu) {
    		return menu.get("order");
    	},
	});
});