define([
    'backbone',
    'models/Menu',
], function(Backbone, MenuModel) {
	return Backbone.Collection.extend({

	    model: MenuModel,
	});
});