define([
    'jquery.ui',
    'backbone',
    'marionette',
    'views/SingleTask',
    'text!templates/TaskList.html'
], function(ui, Backbone, Marionette, SingleTaskView, tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	template: tpl,
    	itemView: SingleTaskView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
        
    });
});