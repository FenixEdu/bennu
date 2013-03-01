define([
    'jquery',
    'backbone',
    'marionette',
    'menu-manager',
    'text!templates/HostList.html'
], function($, Backbone, Marionette, MenuManager, tpl) {

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
		MenuManager.Application.navigate("#hosts/edit/" + e.target.id, true);
	}
	
});
});