define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/MenuModelAdd.html',
    'models/Menu',
], function($, Backbone, Marionette, App, tpl, Menu) {
	

	return Backbone.Marionette.ItemView.extend({
		
		template: tpl,
		tagName: "div",
		className: "modal hide fade",
		attributes: {
			id: "modal-add-menu"
		},
		
		events : {
			"click #add-menu-confirm" : "addMenu"
		},
		
		addMenu : function (e) {
			var menu = this.parentMenu.menu;
			var order = 1;
			if (menu && menu.length > 0) {
				order = $(menu.pluck("order")).last().get(0) + 1;
			}
			var selectedPath = $("#select-menu").val();
			var model;
			
			if (selectedPath === "custom") {
				model = {title : {"pt" : "Custom Menu"} , order : order};
			} else {
				model = {path : selectedPath , order : order};
			}
			
			var newMenuModel = new Menu(model);
			var that = this;
			newMenuModel.save(null,{success: function() {
				newMenuModel.initialize();
				var menu = that.parentMenu.menu;
				menu.push(newMenuModel);
				that.parentMenu.set({menu : menu});
				that.parentMenu.save();
			}});
		}
	});
});
	