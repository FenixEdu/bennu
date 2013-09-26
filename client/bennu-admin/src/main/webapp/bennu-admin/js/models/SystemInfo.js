define([ 'backbone', ], function(Backbone) {
    return Backbone.Model.extend({
        urlRoot : "../api/bennu-core/system/info",
    });
});