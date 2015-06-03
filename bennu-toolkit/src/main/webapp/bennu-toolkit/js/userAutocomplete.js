/*
 * userAutocomplete.js
 * 
 * Copyright (c) 2014, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of Bennu Toolkit.
 * 
 * Bennu Toolkit is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Bennu Toolkit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Bennu Toolkit. If not, see
 * <http://www.gnu.org/licenses/>.
 */

(function () {
    Bennu.userAutocomplete = Bennu.userAutocomplete || {};
    
    Bennu.userAutocomplete.attr = "bennu-user-autocomplete";
    
    Bennu.userAutocomplete.setup = function () {
    	
    	var bloodhound = null;
    	
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
    	
        	return bloodhound;
    };
    
    Bennu.userAutocomplete.bloodhound = Bennu.userAutocomplete.bloodhound || Bennu.userAutocomplete.setup();

    
    Bennu.userAutocomplete.createWidget = function (input) {
    	
        var input = $(input);

        var widget = $('<div class="bennu-user-autocomplete-input-group input-group"><input type="text" class="bennu-user-autocomplete-input form-control"/>'+
            '<span class="input-group-addon"><span class="glyphicon glyphicon-user"></span></span></div>');

        var userAutocomplete = $("input", widget);

        var attrs = input.prop('attributes');
        
        $.each(attrs, function() {
        	if (this.name === Bennu.userAutocomplete.attr ||
        		this.name === "name" ||
        		this.name === "id" || this.name === "value") {
        		return true;
        	}
            var currValue = userAutocomplete.attr(this.name);
            userAutocomplete.attr(this.name, (currValue ? currValue + ' ' : '') + this.value);
        });
        
        
        input.attr('type','hidden');
        userAutocomplete.show();
        
        var id = "user-autocomplete-" + Bennu.gensym();
        
        userAutocomplete.attr('id', id);
        
        widget.insertAfter(input);
        
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

        userAutocomplete.on('keyup', function (event) {
            var text = userAutocomplete.val();
            var value = result.get();
            if(value && value.username != text) {
                // Clear the result from the underlying model, but keep the text
                result.clear();
                userAutocomplete.typeahead('val', text);
            }
        });
        
        result.onchange(function() {
        	var value = this.get();
        	if (value && value.hasOwnProperty('username') && value.hasOwnProperty('name')) {
        		$(input).val(value.username);
        		userAutocomplete.typeahead('val', value.name);
        	} else {
        		$(input).val("");
        		userAutocomplete.typeahead('val', '');
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
    	    source: Bennu.userAutocomplete.bloodhound.ttAdapter(),
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
        
        var val = $(input).val();
        
        if (val) {
        	$.ajax({
        		url: Bennu.contextPath + "/api/bennu-core/users/find?query=" + val + "&maxHits=1",
        		dataType:"json",
        		method:"POST",
        		success: function(data) {
        			result.set(data.users[0]);
        		}
        	});
        }
        
        return result;
    };


})();
