define([
    'jquery.datatables',
    'jquery.ui',
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
            'change': 'xrender',
            'destroy': 'render'
    	},
        
        collectionEvents: {
        	'change' : 'xrender',
        },
        
        events: {
        	"click #refresh" : "refreshButton" 
        },
        
        refreshButton: function(e) {
        	this.toggleRefresh($(e.target).is(":checked"));
        },
        
        xrender: function() {
        	this.render();
        },

        initSortingMethod: function() {
            $.extend( $.fn.dataTableExt.oSort, {
                "schedule-date-pre": function ( date ) {
                    return moment(date, "DD MMMM YYYY HH:mm:ss").format("X");
                },
 
                "schedule-date-asc": function ( a, b ) {
                    return a - b;
                },
             
                "schedule-date-desc": function ( a, b ) {
                    return b - a;
                }
            } );
        },
        
        enableDataTable : function() {
            this.initSortingMethod();
            $("table").dataTable( {
        		"bFilter": true,
        		"bScrollInfinite": true,
        		"sScrollY": "400px",
        		"aaSorting": [[2, "desc"]],
        		"aoColumns": [
        		              null,
        		              { "sType": "schedule-date"},
        		              { "sType": "schedule-date"},
        		              { "sType": "html" },
        		              null]
        	});
        },
        
        onDomRefresh: function() {
            this.enableDataTable();
        	$("#refresh").attr('checked', this.interval != undefined);
        },
        
        clearRefresh: function() {
        	if (this.interval != undefined) {
    			clearInterval(this.interval);
    			this.interval = undefined;
    			$("#refresh").attr('checked',false);
    		}
        },
        
        activateRefresh: function() {
        	if (this.interval === undefined) {
        		var that = this;
    			this.interval = setInterval(function() {
    				that.collection.fetch();
    				that.render();
    			}, 3000);
    			$("#refresh").attr('checked',true);
        	}
        },
        
        toggleRefresh: function(value) {
        	if (value) {
        		this.activateRefresh();
        	} else {
        		this.clearRefresh();
        	}
        },
        
        onShow: function() {
        	//this.activateRefresh();
    	},
    	
    	onClose: function() {
    		this.clearRefresh();
    	}
    });
});