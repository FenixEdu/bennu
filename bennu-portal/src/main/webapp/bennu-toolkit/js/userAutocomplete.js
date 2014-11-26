(function () {
    Bennu.userAutocomplete = Bennu.userAutocomplete || {};
    
    Bennu.userAutocomplete.attr = "bennu-user-autocomplete";
    
    var bloodhound = null;
    
    Bennu.userAutocomplete.setup = function () {
    	
    	if (!bloodhound) {
    		bloodhound = new Bloodhound({
        	    datumTokenizer: function (d) {
        	        return Bloodhound.tokenizers.whitespace(d.value);
        	    },
        	    queryTokenizer: Bloodhound.tokenizers.whitespace,
        	    limit:10,
        	    remote: {
        	        url: Bennu.contextPath + "/api/bennu-core/users/find",
        	
        	        replace: function (url, query) {
        	            return url + "?query=" + query + "&maxHits=10";
        	        },
        	
        	        ajax: {
        	            beforeSend: function (jqXhr, settings) {
        	                //settings.data = $.param({q: queryInput.val()});
        	            },
        	            type: "POST"
        	
        	        },
        	
        	        filter: function (response) {
        	            return response.users;
        	        }
        	    }
        	});
        	bloodhound.initialize();
    	}
    	
    };
    
    Bennu.userAutocomplete.createWidget = function (input) {
    	
        var input = $(input);
        
        var userAutocomplete = $("<input type='text'/>");
        
        var attrs = input.prop('attributes');
        
        $.each(attrs, function() {
        	if (this.name === Bennu.userAutocomplete.attr ||
        		this.name === "name" ||
        		this.name === "id") {
        		return true;
        	}
        	userAutocomplete.attr(this.name, this.value);
        });
        
        input.attr('type','hidden');
        
        var id = "user-autocomplete-" + Bennu.gensym();
        
        userAutocomplete.attr('id', id);
        
        userAutocomplete.insertAfter(input);
        
        var events = [];
        
        var result = {
        		
        		input : input,
        		value : undefined,
        		
		        get:function(){
		            return this.value;
		        },
		
		        set:function(val){
		            this.value = val;
		            this.trigger("change");
		        },
		
		        clear:function(val){
		            this.set("");
		        },
		
		        onchange:function(fn){
		            events.push(fn);
		        },
		
		        trigger: function(){
		        	var self = this;
		            for (var i = 0; i < events.length; i++) {
		                events[i].apply(self,{
		                    type:"change",
		                    target:this
		                });
		            }
		        },
        };
        
        result.onchange(function() {
        	var value = this.get();
        	if (value && value.hasOwnProperty('id') && value.hasOwnProperty('name')) {
        		input.val(value.id);
        		userAutocomplete.val(value.name);
        	} else {
        		input.val("");
        		userAutocomplete.val("");
        	}
        });
        
        $('#' + id).typeahead({
    	    hint: true,
    	    highlight: true,
    	    minLength: 3,
    	}, {
    	    name: 'username',
    	    displayKey: function(user) {
    	    	return user.displayName;
    	    },
    	    source: bloodhound.ttAdapter(),
    	    templates: {
    	        empty: [
    	            '<div class="empty-message">',
    	            'No User Found',
    	            '</div>'
    	        ].join('\n'),
    	        suggestion: function (x) {
    	            return '<p><div class="row">' +
    	                '<div class="col-xs-1"><img class="img-circle" src="' + x.avatar + '?s=32" alt="" /></div>' +
    	                '<div class="col-sm-11">' +
    	                '<div>' + x.displayName + '</div>' +
    	                '<div>' + x.username + '</div>' +
    	                '</div></div></p>';
    	        }
    	    }
    	}).on("typeahead:autocompleted typeahead:selected", function (el, user, elName) {
    		result.set(user);
    	});
        
        result.handler = Bennu.widgetHandler.makeFor(input);
        
        return result;
    };


})();
