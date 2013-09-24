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
	           $.post("../api/bennu-scheduler/tasks/" + taskName).always(function() {
	               App.Router.navigate("#logs", true);
	           });
	       }

	});
});