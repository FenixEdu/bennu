define([
    'moment',
    'models/Log',
], function(moment, LogModel) {
	return LogModel.extend({

		baseUrl: "../api/bennu-scheduler/custom/",

	});
});