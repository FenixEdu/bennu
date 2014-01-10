define([
    'app',
    'views/LogList',
    'views/AddCustomTask',
    'text!templates/CustomLogList.html'
], function(App, LogList, AddCustomTaskView, tpl) {

    return LogList.extend({
    	template: tpl,
    });
});