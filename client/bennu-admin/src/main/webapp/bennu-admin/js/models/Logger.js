define([ 'backbone', ], function(Backbone) {
    return Backbone.Model.extend({
    	parse: function(model) {
    		$(model.loggers).each(function (index, value) {
    			value[value.level.toLowerCase()] = true;
    		});
    		return model;
    	},
        urlRoot : "../api/bennu-core/system/logger",
    });
});
