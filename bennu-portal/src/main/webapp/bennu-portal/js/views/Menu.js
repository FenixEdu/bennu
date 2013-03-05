define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'views/SingleMenu'
], function($, Backbone, Marionette, App, SingleMenuView) {

    return Backbone.Marionette.CollectionView.extend({
    	
    	itemView: SingleMenuView,
        
    });
});