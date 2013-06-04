define([
    'moment',
    'models/Log',
], function(moment, LogModel) {
	return LogModel.extend({

		urlRoot: "../api/bennu-scheduler/custom",

	});
});