define([
    'jquery-datatables',
    'jquery-ui',
    'backbone',
    'marionette',
    'views/SingleLog',
    'text!templates/LogList.html'
], function(jdt, ui, Backbone, Marionette, SingleLogView, tpl) {

    return Backbone.Marionette.CompositeView.extend({
    	template: tpl,
    	itemView: SingleLogView,
    	itemViewContainer: "tbody",
    	
    	modelEvents: {
            'change': 'render',
            'destroy': 'render'
            },
        
        onShow: function() {
        	$("table").dataTable( {
        		"bFilter": false,
        		"bScrollInfinite": true,
        		"sScrollY": "400px",
        		"aoColumns": [
        		              null,
        		              null,
        		              null,
        		              { "sType": "html" },
        		              null]
        	});
        }
        
    });
});