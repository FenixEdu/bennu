define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'collections/Storage',
    'text!templates/SingleStorageConfiguration.html',
], function($, Backbone, Marionette, App, StorageCollection, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        tagName : 'tr',
        
        modelEvents: {
        	"change" : "render"
        },
        
    	serializeData: function() {
			var sc = new StorageCollection();
			var config = this.model.toJSON();
			sc.fetch({async:false, success: function() {
				config.availableRepositories = sc.toJSON();
			}});
			
			$(config.availableRepositories).each(function(i,e) {
				e.selected = false;
				if(config.storage && e.id === config.storage.id) {
					e.selected = true;
				}
				e.storageId = config.id;
			});	
			return config;
        }
        
    });
});