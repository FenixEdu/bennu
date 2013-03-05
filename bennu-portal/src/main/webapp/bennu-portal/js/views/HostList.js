define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/HostList.html',
    'views/SingleHost'
], function($, Backbone, Marionette, App, tpl, SingleHostView) {

    return Backbone.Marionette.CompositeView.extend({

        template: tpl,
        itemView: SingleHostView,
        itemViewContainer: "tbody",

	events: {
		"click .edit-host" : "editHost",
		"click .create-host": "createHost",
		"click .delete-host": "deleteHost",
		"click .show-menu" : "showMenu",
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
	
	showMenu : function(e) {
		Backbone.history.navigate("#menu/" + e.target.id, true);
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