MenuManager.Collection.HostCollection = Backbone.Collection.extend({

    url: "api/hosts",

    model: MenuManager.Model.HostModel,
    
    parse: function(response){
        return response.hosts;
     },
     
     comparator: function (collection) {
    	 return collection.get('hostname');
     }

});