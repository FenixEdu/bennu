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
		"click .create-host": "createHost",
		"click .delete-host": "deleteHost",
	},
	
	/*itemRemoved : function(viewInstance) {
		console.log(viewInstance);
	},
	
	onBeforeClose: function(){
		console.log('before closing view!');
	  },
	  
	onClose: function() {
		console.log('closing view!');
	},*/
	
	
		
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
	},
		
	createHost: function(e) {
		e.preventDefault();
		Backbone.history.navigate("#hosts/create" , true);
	},
	
	deleteHost : function(e) {
		var hostModel = this.collection.get(e.target.id);
		var that = this;
		hostModel.destroy({
            success:function () {
                alert('Host apagado!');
                that.render();
            }
        });
		return false;
	}
	
});
});