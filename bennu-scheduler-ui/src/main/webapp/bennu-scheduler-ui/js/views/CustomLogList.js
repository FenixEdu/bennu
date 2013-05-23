define([
    'app',
    'views/LogList',
    'views/AddCustomTask',
    'text!templates/CustomLogList.html'
], function(App, LogList, AddCustomTaskView, tpl) {

    return LogList.extend({
    	template: tpl,
    	
    	
    	events : {
    		"click .btn-add-custom-task" : "addCustomTask",
    	},
    	
    	
    	addCustomTask: function() {
    		Backbone.history.navigate("#custom/add", true);
    	},
    
    });
});