define([ 'jquery', 'backbone', 'marionette', 'app', 'text!templates/Logger.html' ],

function($, Backbone, Marionette, App, tpl) {
    return Backbone.Marionette.ItemView.extend({
        template : tpl,

        events : {
        	'click .btn' : 'toggleLevel',
        },
        
        toggleLevel: function(e) {
			var el = $(e.currentTarget);
			var logger = el.parent().attr('logger');
			var level = el.html();
			$.ajax({
				type: "GET",
				url: contextPath + '/api/bennu-core/system/logger/' + logger + '/' + level,
				contentType: "application/json; charset=UTF-8",
				success: function() {
					location.reload();
				}
			});
        }
    });
});
