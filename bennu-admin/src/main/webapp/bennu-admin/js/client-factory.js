define([
    'collections/Host',
    'collections/Menu',
    'collections/App',
    'collections/Theme',
    'models/Host',
    'models/Menu',
    'models/App',
    'models/Theme',
    'views/HostCreate',
    'views/HostList',
    'views/SingleMenu',
    'views/Menu',
    'views/MenuCreate',
    'views/App',
], function(HostCollection, MenuCollection, AppCollection,ThemeCollection,
			HostModel, MenuModel, AppModel, ThemeModel, 
			HostCreateView, HostListView, SingleMenuView, MenuView, MenuCreateView,AppView) {
	var MenuManager = MenuManager || {};
	MenuManager.DB = MenuManager.DB || {};

	MenuManager.Collections = MenuManager.Collections || {};
	MenuManager.Collections.Host = HostCollection || {};
	MenuManager.Collections.Menu = MenuCollection || {};
	MenuManager.Collections.App = AppCollection || {};
	MenuManager.Collections.Theme = ThemeCollection || {};
	
	MenuManager.Models = MenuManager.Models || {};
	MenuManager.Models.Host = HostModel || {};
	MenuManager.Models.Menu = MenuModel || {};
	MenuManager.Models.App = AppModel || {};
	MenuManager.Models.Theme = ThemeModel || {};
	
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