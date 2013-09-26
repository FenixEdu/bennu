define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'views/SingleStorageConfiguration',
    'text!templates/StorageConfigurationList.html',
], function(ui, Backbone, Marionette, App, SingleStorageConfigurationView,tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template:tpl,
    	itemView: SingleStorageConfigurationView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
            
        events: {
        	"click .save-configuration" : "saveConfiguration",
        },
        
        saveConfiguration: function(e) {
        	var configs = new Array();
        	$(".select-configuration :selected").each(function(i,option){
        		var config = {};
        		config.fileStorageConfigurationId = $(option).attr('storageId');
        		config.fileStorageId = $(option).val();
        		configs.push(config);
        	});
        	console.log(JSON.stringify(configs));
            $.ajax({
                url : '../api/bennu-io/storage/config',
                contentType : 'application/json',
                data : JSON.stringify(configs),
                dataType : 'json',
                success : function(data) {
                    App.Router.navigate("configuration", {trigger: true});
                },
                processData : false,
                type : 'POST',
            });
        }
            
    });
});