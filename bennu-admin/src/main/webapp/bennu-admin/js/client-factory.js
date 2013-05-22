define([
    'collections/Host',
    'collections/Menu',
    'collections/App',
    'collections/Theme',
    'models/Host',
    'models/Menu',
    'models/App',
    'models/Theme',
    'models/SystemInfo',
    'views/HostCreate',
    'views/HostList',
    'views/SingleMenu',
    'views/Menu',
    'views/MenuCreate',
    'views/App',
    'views/SystemInfo',
], function(HostCollection, MenuCollection, AppCollection,ThemeCollection,
			HostModel, MenuModel, AppModel, ThemeModel, SystemInfoModel,
			HostCreateView, HostListView, SingleMenuView, MenuView, MenuCreateView,AppView, SystemInfoView) {
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
	MenuManager.Models.SystemInfo = SystemInfoModel || {};
	
	MenuManager.Views = MenuManager.Views || {};
	MenuManager.Views.HostCreate = HostCreateView || {};
	MenuManager.Views.HostList = HostListView || {};
	MenuManager.Views.Menu = MenuView || {};
	MenuManager.Views.SingleMenu = SingleMenuView || {};
	MenuManager.Views.MenuCreate = MenuCreateView || {};
	MenuManager.Views.App = AppView || {};
    MenuManager.Views.SystemInfo = SystemInfoView || {};
	
	MenuManager.Router = MenuManager.Router || {};
	MenuManager.Layout = MenuManager.Layout || {};
	
	return MenuManager;

});