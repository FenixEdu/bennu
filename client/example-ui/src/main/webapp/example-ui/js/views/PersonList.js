define([
    'marionette',
    'models/Person',
    'text!templates/PersonList.html',
    "i18n!nls/messages",
], function(Marionette,Person, tpl, messages) {

	var PersonView = Marionette.ItemView.extend({
		tagName: 'tr',
		getTemplate:function() {
			if (this.model.get("number") === 1) {
				return "#person-special-template";
			}
			return "#person-template";
		}
	});
	
    return Marionette.CompositeView.extend({
    	template:"#people-template",
    	itemView: PersonView,
    	itemViewContainer: "tbody",
    	
    	collectionEvents: {
    		'change': 'render',
    	},
    	
    	events: {
    		"click #add" : "addPerson"
    	},
    	
    	onBeforeRender: function() {
    		this.templates = $(tpl);
    		$("head").append(this.templates);
    	},
    
    	onClose: function() {
    		this.templates.remove();
    	},
    	
    	addPerson : function() {
    		var person = new Person({name : "Nome", number: 4, Location : "Somewhere"});
    		this.collection.push(person);
    	}
    });
    
});