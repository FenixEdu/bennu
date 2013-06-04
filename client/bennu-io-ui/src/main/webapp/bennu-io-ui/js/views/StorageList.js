define([
    'jquery-bootstrap',
    'backbone',
    'marionette',
    'views/SingleStorage',
    'models/LFSStorage',
    'text!templates/StorageList.html'
], function(jqb, Backbone, Marionette,  SingleStorageView, LFSStorageModel, tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	template: tpl,
    	itemView: SingleStorageView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
        
        events : {
        	"click #createNewRepoTab a" : "selectTab",
        	"click .submit-lfsstorage" : "submitLfsStorage",
        	"click .submit-domainstorage" : "submitDomainStorage",
        },
        
        selectTab : function(e) {
        	e.preventDefault();
        	//jqb(e.target).tab('show');
        },
        
        submitLfsStorage: function(evt) {
        	evt.preventDefault();
        	var that = this;
        	var data = {name : $("#createLFSStorageForm input[field=name]").val(),
      			   path : $("#createLFSStorageForm input[field=path]").val(),
     			   treeDirectoriesNameLength : $("#createLFSStorageForm input[field=treeDirectoriesNameLength]").val()};
        	var model = new LFSStorageModel();
        	model.save(data, {success: function() {
        		that.collection.push(model);
        	}});
    	},
    	
    	submitDomainStorage: function(evt) {
    		evt.preventDefault();
    		var that = this;
    		var name = $("#createDomainStorageForm input[field=name]").val();
    		$.post("../api/bennu-io/storage/domain/" + name, null, function(data) {
    			require(['models/Storage'], function(StorageModel) {
    				that.collection.push(new StorageModel(data));
    			});
    		});
    	},
    });
});
