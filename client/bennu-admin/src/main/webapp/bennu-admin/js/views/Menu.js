define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'views/SingleMenu',
], function(jQueryUI, Backbone, Marionette, App, SingleMenuView) {

    return Backbone.Marionette.CollectionView.extend({
    	
    	tagName: 'ul',
    	className: 'menu-root connect-menus',
    	itemView: SingleMenuView,
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
            
        makeSortable: function() {
        	$('#menu-tree ul').sortable({
        		//connectWith:".connect-menus",
                stop: function(event, ui) {
                    ui.item.trigger('drop', ui.item.index());
                }
            });
        },
        	
    	onRender: function() {
    		this.makeSortable();
    		//console.log("show ...");
    	},
        
    });
});