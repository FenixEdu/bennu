define([ 'jquery', 'underscore', 'mustache', 'backbone', 'marionette', 
         'app', 'models/Storage', 'collections/Storage', 'views/StorageList', 'collections/StorageConfiguration', 
         'views/StorageConfiguration' ],
		function($, _, Mustache, Backbone, Marionette, 
				App, StorageModel, StorageCollection, StorageListView, StorageConfigurationCollection, 
				StorageConfigurationListView) {

			var Router = Backbone.Marionette.AppRouter.extend({

				initialize : function() {
					console.log("initialize controller ...");
				},

				appRoutes : {
					"" : "showStorages",
					"storage" : "showStorages",
					"configuration" : "showStoragesConfiguration",
				},

				controller : {
					
					
					showStorages : function(type) {
						var storageCollection = new StorageCollection();
						storageCollection.fetch({success : function() {
								App.page.show(new StorageListView({
									collection : storageCollection,
								}));
						}});
					},

					showStoragesConfiguration : function() {
						var storageConfigCollection = new StorageConfigurationCollection();
						storageConfigCollection.fetch({success: function() {
							App.page.show(new StorageConfigurationListView({
								collection : storageConfigCollection
							}));
						}});
					},

				}
			});

			return Router;

		});