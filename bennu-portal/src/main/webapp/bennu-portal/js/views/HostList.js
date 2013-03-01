define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/HostList.html'
], function($, Backbone, Marionette, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,

	events: {
		"click .edit-host" : "editHost",
	},
	
		
	serializeData: function () {
		return { hosts : this.collection.toJSON(),
				 mls : function() { 
							return function(val) { 
								return this[val].pt;
							};
						}
			   };
		},
	
	editHost : function(e) {
		e.preventDefault();
		Backbone.history.navigate("#hosts/edit/" + e.target.id, true);
	}
	
});
});