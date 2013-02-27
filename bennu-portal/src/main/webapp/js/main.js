MenuManager.Router.Main = Backbone.Router.extend({

	routes : {
		"" : "verifyLogin",
		"login" : "showLogin",
		"home" : "showHome",
		"hosts": "showHosts",
		"hosts/edit/:id" : "editHost",
	},
	
	showLogin : function () {
		var that = this;
		var settingsModel = new MenuManager.Model.ApplicationSettingsModel();
		settingsModel.fetch({
			success : function() {
					if (settingsModel.get("casEnabled"))
						window.location.href = settingsModel.get("loginUrl");
					else
						MenuManager.Util.renderTemplate("LoginView", $("body"));
				}
			});
	},
	
	verifyLogin : function() {
		var loginModel = new MenuManager.Model.LoginModel();
		var that = this;
		loginModel.fetch({
			success : function() {
				that.showHome(loginModel);
			}
		});
	},

	initialize : function() {
	},

	showHome : function(loginModel) {
			var headerView = new MenuManager.View.HeaderView({ model : loginModel });
			headerView.render();
			var footerView = MenuManager.Util.getFooterView();
			footerView.render();
			var homeView = new MenuManager.View.HomeView();
			homeView.render();
	},

	showUser : function(id) {
		var userModel = new MenuManager.Model.UserModel({
			id : id
		});
		userModel.fetch({
			success : function() {
				var userView = new MenuManager.View.UserView({
					model : userModel
				});
				userView.render();
			}
		});
	},

	showUsers : function() {
		var userCollection = new CoffeeManager.Collection.UserCollection();
		userCollection.fetch({
			success : function() {
				var userListView = new CoffeeManager.View.UserListView({
					collection : userCollection
				});
				userListView.render();
			}
		});
	},
	
	showHosts : function() {
		var hostCollection = new MenuManager.Collection.HostCollection();
		hostCollection.fetch({
			success : function() {
				var hostListView = new MenuManager.View.HostListView({
					collection : hostCollection
				});
				hostListView.render();
			}
		});
	},

	createHost : function() {
		var createHostView = new MenuManager.View.CreateHostView();
		createHostView.render();
	},
	
	editHost : function(id) {
		
	}
	
	
//	showCoffeeOrder : function(id) {
//		var coffeOrderModel = new MenuManager.Model.CoffeeOrderModel({
//			id : id
//		});
//		coffeOrderModel.fetch({
//			success : function() {
//				var coffeeOrderView = new MenuManager.View.CoffeeOrderView({
//					model : coffeOrderModel
//				});
//				coffeeOrderView.render();
//			}
//		});
//	},
//
//	editCoffeeOrder : function(id) {
//		var itemCollection = new MenuManager.Collection.CoffeeItemCollection();
//		itemCollection.fetch({
//			success : function() {
//				var editCoffeeOrderView = new MenuManager.View.EditCoffeeOrderView({
//					id : id,
//					collection : itemCollection
//				});
//				editCoffeeOrderView.render();
//			}
//		});
//	},
//
//	showCoffeeOrders : function() {
//		var orderCollection = new MenuManager.Collection.CoffeeOrderCollection();
//		orderCollection.fetch({
//			success : function() {
//				var orderListView = new MenuManager.View.CoffeeOrderListView({
//					collection : orderCollection
//				});
//				orderListView.render();
//			}
//		});
//	},
//
//	showCoffeeBatch : function(id) {
//		var batchModel = new MenuManager.Model.CoffeeBatchModel({
//			id : id
//		});
//		batchModel.fetch({
//			success : function() {
//				var batchView = new MenuManager.View.CoffeeBatchView({
//					model : batchModel
//				});
//				batchView.render();
//			}
//		});
//	},
//
//	showCoffeeBatches : function() {
//		var batchCollection = new MenuManager.Collection.CoffeeBatchCollection();
//		batchCollection.fetch({
//			success : function() {
//				var batchListView = new MenuManager.View.CoffeeBatchListView({
//					collection : batchCollection
//				});
//				batchListView.render();
//			}
//		});
//	},
//
//	showCoffeeItems : function() {
//		var itemCollection = new MenuManager.Collection.CoffeeItemCollection();
//		itemCollection.fetch({
//			success : function() {
//				var itemListView = new MenuManager.View.CoffeeItemListView({
//					collection : itemCollection
//				});
//				itemListView.render();
//			}
//		});
//	},
//
//	createCoffeeItem : function() {
//		var createItemView = MenuManager.View.CreateCoffeeItemView();
//		createItemView.render();
//	}
});

MenuManager.Application = new MenuManager.Router.Main();
Backbone.emulateJSON = true;

Backbone.ajaxSync = Backbone.sync;

Backbone.customSync = function(method, model, option) {
	option.error = function(response, textStatus, errorThrown) {
		if (response.status === 401) {
			MenuManager.Application.navigate("login", true);
		}
	}
	return Backbone.ajaxSync(method, model, option);
}

Backbone.sync = Backbone.customSync;
Backbone.history.start();
