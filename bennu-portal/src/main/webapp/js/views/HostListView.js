MenuManager.View.HostListView = Backbone.View.extend({
	
	el: $('#content'),

	events: {
		"click .edit-host" : "editHost",
	},
	
	
	editHost : function(e) {
		e.preventDefault();
		MenuManager.Application.navigate("#hosts/edit/" + e.target.id, true);
	},
	
    render: function () {
    	MenuManager.Util.renderTemplate("HostListView", this.el, { hosts: this.collection.toJSON(), mls : function() { 
		return function(val) { 
				return this[val].pt;
			  } 
	}});
        return this;
    }
});