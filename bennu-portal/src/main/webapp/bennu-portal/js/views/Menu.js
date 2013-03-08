define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'views/SingleMenu',
], function($, Backbone, Marionette, App, SingleMenuView) {

    return Backbone.Marionette.CollectionView.extend({
    	
    	tagName: 'ul',
    	itemView: SingleMenuView,
    	
    	modelEvents: {
            'change': 'render'
            },
            
        makeSortable: function() {
        	$('#menu-tree ul').sortable({
                stop: function(event, ui) {
                    ui.item.trigger('drop', ui.item.index());
                }
            });
        },
        	
    	onShow : function() {
    		this.makeSortable();
    	},
    	
    	onRender: function() {
    		this.makeSortable();
    		console.log("show ...");
    	}
        
    });
});