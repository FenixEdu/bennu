define([
    'jquery-ui',
    'backbone',
    'marionette',
    'views/SingleSchedule',
    'text!templates/ScheduleList.html'
], function(ui, Backbone, Marionette, SingleScheduleView,tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	
    	template:tpl,
    	itemView: SingleScheduleView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
        
    });
});