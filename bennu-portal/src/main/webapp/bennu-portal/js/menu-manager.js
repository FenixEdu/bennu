define([
    'collections/Host',
    'collections/Menu',
    'models/Host',
    'models/Menu',
    'views/HostCreate',
    'views/HostList',
    'views/SingleMenu',
    'views/Menu',
    'views/MenuCreate',
], function(HostCollection, MenuCollection, HostModel, MenuModel, HostCreateView, HostListView, SingleMenuView, MenuView, MenuCreate) {
	var MenuManager = MenuManager || {};

	/** THE PLACE TO STORE THE COLLECTIONS AND MODELS **/
	MenuManager.DB = MenuManager.DB || {};

	MenuManager.Collections = MenuManager.Collections || {};
	MenuManager.Collections.Host = HostCollection || {};
	MenuManager.Collections.Menu = MenuCollection || {};
	
	MenuManager.Models = MenuManager.Models || {};
	MenuManager.Models.Host = HostModel || {};
	MenuManager.Models.Menu = MenuModel || {};
	
	MenuManager.Views = MenuManager.Views || {};
	MenuManager.Views.HostCreate = HostCreateView || {};
	MenuManager.Views.HostList = HostListView || {};
	MenuManager.Views.Menu = MenuView || {};
	MenuManager.Views.SingleMenu = SingleMenuView || {};
	MenuManager.Views.MenuCreate = MenuCreate || {};
	
	MenuManager.Router = MenuManager.Router || {};
	MenuManager.Layout = MenuManager.Layout || {};
	
	return MenuManager;

});