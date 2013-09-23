define([ 'jquery', 'backbone', 'marionette', 'app', 'text!templates/SingleTask.html' ], function($, Backbone,
		Marionette, App, tpl) {

	return Backbone.Marionette.ItemView.extend({

		template : tpl,
		tagName : 'tr',

		modelEvents : {
			"change" : "render"
		},

		events : {
			"click #run-now" : "runNow",
		},

		runNow : function() {
			var taskName = this.model.get("type");
			var runNowURL = "../api/bennu-scheduler/tasks/" + taskName;
			$.post({
				url : runNowURL,
				async : false,
				cache : false,
				success : function() {
					App.navigate("#logs", true);
				}
			});
		}

	});
});