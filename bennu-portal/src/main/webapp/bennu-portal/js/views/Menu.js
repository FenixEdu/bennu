define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'views/SingleMenu',
], function($, Backbone, Marionette, App, SingleMenuView) {

    return Backbone.Marionette.CollectionView.extend({
    	
    	tagName: 'ul',
    	itemView: SingleMenuView,
    	
    	onShow : function() {
    		$('#menu-tree ul').sortable({
                stop: function(event, ui) {
                    ui.item.trigger('drop', ui.item.index());
                }
            });
    		console.log("show ...");
    	},
        
    });
});