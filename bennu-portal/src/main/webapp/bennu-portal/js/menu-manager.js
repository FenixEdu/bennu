define([
    'collections/Host',
    'models/Host',
    'views/HostCreate',
    'views/HostList',
    'backbone',
    'marionette'
], function(HostCollection, HostModel, HostCreateView, HostListView, Backbone, Marionette) {
	var MenuManager = MenuManager || {};

	/** THE PLACE TO STORE THE COLLECTIONS AND MODELS **/
	MenuManager.DB = MenuManager.DB || {};

	MenuManager.Collections = MenuManager.Collections || {};
	MenuManager.Collections.Host = HostCollection || {};
	
	MenuManager.Models = MenuManager.Models || {};
	MenuManager.Models.Host = HostModel || {};
	
	MenuManager.Views = MenuManager.Views || {};
	MenuManager.Views.HostCreate = HostCreateView || {};
	MenuManager.Views.HostList = HostListView || {};
	
	MenuManager.Router = MenuManager.Router || {};
	
	MenuManager.Layout = MenuManager.Layout || {};
	
	return MenuManager;

});