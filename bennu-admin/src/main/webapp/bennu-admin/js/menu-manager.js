define([
    'collections/Host',
    'collections/Menu',
    'collections/App',
    'models/Host',
    'models/Menu',
    'models/App',
    'views/HostCreate',
    'views/HostList',
    'views/SingleMenu',
    'views/Menu',
    'views/MenuCreate',
    'views/App',
], function(HostCollection, MenuCollection, AppCollection, 
			HostModel, MenuModel, AppModel, 
			HostCreateView, HostListView, SingleMenuView, MenuView, MenuCreateView,AppView) {
	var MenuManager = MenuManager || {};
	MenuManager.DB = MenuManager.DB || {};

	MenuManager.Collections = MenuManager.Collections || {};
	MenuManager.Collections.Host = HostCollection || {};
	MenuManager.Collections.Menu = MenuCollection || {};
	MenuManager.Collections.App = AppCollection || {};
	
	MenuManager.Models = MenuManager.Models || {};
	MenuManager.Models.Host = HostModel || {};
	MenuManager.Models.Menu = MenuModel || {};
	MenuManager.Models.App = AppModel || {};
	
	MenuManager.Views = MenuManager.Views || {};
	MenuManager.Views.HostCreate = HostCreateView || {};
	MenuManager.Views.HostList = HostListView || {};
	MenuManager.Views.Menu = MenuView || {};
	MenuManager.Views.SingleMenu = SingleMenuView || {};
	MenuManager.Views.MenuCreate = MenuCreateView || {};
	MenuManager.Views.App = AppView || {};
	
	MenuManager.Router = MenuManager.Router || {};
	MenuManager.Layout = MenuManager.Layout || {};
	
	return MenuManager;

});