define([
    'jquery',
    'backbone',
    'marionette',
    'app',
    'text!templates/HostCreate.html'
], function($, Backbone, Marionette, App, tpl) {
	

	return Backbone.Marionette.ItemView.extend({

		template: tpl,
		
		events : {
	    	"click #confirm" : "createHost",
	    	"change #logo" : "changeLogo",
	    },
	    
	    changeLogo: function(e) {
	    	var files = e.target.files; // FileList object
	    	var that = this;
	    	$("#error").empty();
	    	$("#confirm").attr('disabled', 'disabled');
	    	for (var i = 0; i < files.length; i++) {
	    	    	var file = files[i];
	    	    	if (!file.type.match("image.*")) {
	    	    		$("#error").append("<p>Apenas são aceites imagens</p>");
	    	    		$("#logo").val("");
	    	    		$("#confirm").removeAttr('disabled');
	    	    		continue;
	    	    	}
	    	    	
	    	    	if (file.size > 200 * 1024) { //200kb
	    	    		$("#error").append("<p>Imagem muito grande. Tamanho máximo : 200kb</p>");
	    				$("#logo").val("");
	    				$("#confirm").removeAttr('disabled');
	    				continue;
	    	    	}
	    	    	
	    	    	var reader = new FileReader();
	    	    	reader.onload = (function(f) {
	    	    		return function(e) {
	    	    			var content = e.target.result;
	    	    			var picBase64 = content.substr(content.indexOf(",") + 1, content.length);	    	    			
	    	    			that.model.set({logo : picBase64, logoType: file.type});
	    	    			$("#image-logo").attr('src', content);
	    	    			$("#confirm").removeAttr('disabled');
	    	    		};
	    	    	})(file);
	    	    	reader.readAsDataURL(file);
	    	}
	    },
	    
	    createHost : function(e) {
	    	var that = this;
	    	e.preventDefault();
	    	$('input').each(function(index, input) {
	    		if ($(input).attr('type') != 'file') {
		    		if ($(input).attr('lang')) {
		    			var mls = that.model.get(input.name) || {};
		    			mls[$(input).attr('lang')] = $(input).val();
		    			that.model.set(input.id, mls);
		    		} else {
		    			that.model.set(input.id, $(input).val());
		    		}
	    		}
	    	});
	    	
	    	this.model.set("theme", $("#theme").val());
	    	
	    	console.log(this.model.toJSON());
	    	
	    	this.model.save(null,{ success : function () {
	    		Backbone.history.navigate("hosts", true);
	    	} });
	    },
	    
	    serializeData: function(e) {
	    	var model = this.model.toJSON();
	    	model.themes = App.themes.toJSON();
	    	$(model.themes).each(function(i,t) {
	    		if (t.name === model.theme){
	    			t.selected = true;
	    		}
	    	});
	    	model.locales = BennuPortal.locales;
	    	
	    	model["_mlsLocale"] = function() {
	    		return function(data) {
	    			return model[data][this.tag];
	    		};
	    	};
	    	
	    	return model;
	    }
	});
});