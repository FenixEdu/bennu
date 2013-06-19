define([
    'backbone',
    'moment',
    'models/Person',
], function(Backbone, moment, PersonModel) {
	return Backbone.Collection.extend({

	    model: PersonModel,
	    
	});
});