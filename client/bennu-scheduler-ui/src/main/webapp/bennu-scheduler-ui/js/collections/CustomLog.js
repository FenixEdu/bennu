define([
    'moment',
    'collections/Log',
], function(moment, LogCollection) {
	return LogCollection.extend({

	    url: "../api/bennu-scheduler/custom",

	});
});