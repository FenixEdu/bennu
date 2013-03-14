define([
    'jquery.ui',
    'backbone',
    'marionette',
    'app',
    'text!templates/App.html',
    'views/SingleApp'
], function($, Backbone, Marionette, App, tpl, SingleAppView) {
	return Backbone.Marionette.CollectionView.extend({
		template: tpl,
		itemView: SingleAppView,
		itemViewContainer: "#app",
		tagName: "ul",
		className:"connect-menus",
		attributes: {
			id : "apps",
		},
		
//		makeSortable: function() {
//			$('.connect-menus').sortable({
//				connectWith:".connect-menus",
//                stop: function(event, ui) {
//                    ui.item.trigger('drop', ui.item.index());
//                }
//            });
//		},
//		
//		onRender: function() {
//			this.makeSortable();
//		}
	});
});
