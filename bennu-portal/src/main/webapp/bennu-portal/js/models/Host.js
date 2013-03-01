define([
    'backbone',
], function(Backbone) {
	return Backbone.Model.extend({
	
	urlRoot : "api/hosts",
	
	defaults: {    
        hostname : "zen.com",
        applicationTitle : {"pt" : "my app pt", en : "my app en"},
        htmlTitle : {pt : "app html title pt", en : "app html title en"},
        applicationSubTitle : {pt : "app subtitle pt", en : "app subtitle en"},
        applicationCopyright : {pt : "app copyright pt", en : "app copyright en"},
        supportEmailAddress : "email@myapp.com",
        systemEmailAddress : "system@myapp.com" 
    },
});

});