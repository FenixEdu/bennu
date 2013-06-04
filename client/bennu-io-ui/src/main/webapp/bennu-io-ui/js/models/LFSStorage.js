define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "../api/bennu-io/storage/lfs",
	
	defaults: {
		name : "title",
		filesCount : 0,
		path : '/tmp',
		treeDirectoriesNameLength: 5,
    },
	});

});