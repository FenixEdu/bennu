define([
    'collections/Menu',
    'collections/App',
    'collections/Theme',
    'models/Menu',
    'models/App',
    'models/Theme',
    'models/PortalConfiguration',
    'models/SystemInfo',
    'views/SingleMenu',
    'views/Menu',
    'views/MenuCreate',
    'views/App',
    'views/PortalConfiguration',
    'views/SystemInfo',
], function(MenuCollection, AppCollection,ThemeCollection,
			MenuModel, AppModel, ThemeModel, PortalConfigurationModel, SystemInfoModel,
		    SingleMenuView, MenuView, MenuCreateView,AppView, PortalConfigurationView, SystemInfoView) {
	var MenuManager = MenuManager || {};
	MenuManager.DB = MenuManager.DB || {};

	MenuManager.Collections = MenuManager.Collections || {};
	MenuManager.Collections.Menu = MenuCollection || {};
	MenuManager.Collections.App = AppCollection || {};
	MenuManager.Collections.Theme = ThemeCollection || {};
	
	MenuManager.Models = MenuManager.Models || {};
	MenuManager.Models.Menu = MenuModel || {};
	MenuManager.Models.App = AppModel || {};
	MenuManager.Models.Theme = ThemeModel || {};
    MenuManager.Models.PortalConfiguration = PortalConfigurationModel || {};
	MenuManager.Models.SystemInfo = SystemInfoModel || {};
	
	MenuManager.Views = MenuManager.Views || {};
	MenuManager.Views.Menu = MenuView || {};
	MenuManager.Views.SingleMenu = SingleMenuView || {};
	MenuManager.Views.MenuCreate = MenuCreateView || {};
	MenuManager.Views.App = AppView || {};
    MenuManager.Views.PortalConfiguration = PortalConfigurationView || {};
    MenuManager.Views.SystemInfo = SystemInfoView || {};
	
	MenuManager.Router = MenuManager.Router || {};
	MenuManager.Layout = MenuManager.Layout || {};
	
	return MenuManager;

});