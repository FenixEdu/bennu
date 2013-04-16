function loadScript(url, callback)
{
    // adding the script tag to the head as suggested before
   var head = document.getElementsByTagName('head')[0];
   var script = document.createElement('script');
   script.type = 'text/javascript';
   script.src = url;

   // then bind the event to the callback function 
   // there are several events for cross browser compatibility
   script.onreadystatechange = callback;
   script.onload = callback;

   // fire the loading
   head.appendChild(script);
}



(function() {
	loadScript("bennu-portal/js/jquery.min.js", function() {
		$("body").hide();
		$.getScript("bennu-portal/js/mustache.min.js", function() {
			$.ajax( {type : "GET",
				 url : "api/bennu-portal/hostmenu/" + window.location.hostname,
				 dataType: "json",
				 success : function(hostJson, status, response) {
					 		var theme_base = "themes/" + hostJson.theme;
					 	   	var theme_url = theme_base + "/layout.html";
					 	   	var styles_url = theme_base + "/css/style.css";
					 	   	var json_handler_url = theme_base + "/js/jsonHandler.js";
					 	   	
					 	   	var BennuPortal = {
					 	   		locales : hostJson.locales,
					 	   		locale: hostJson.locale,
					 	   		lang : (function(locale) {
					 	   				if (locale.indexOf("-") != -1) {
					 	   					return locale.split("-")[0];
					 	   				}
					 	   				return locale;
					 	   		})(hostJson.locale.tag),
					 	   		addMls: function(model) {
					 	   			var lang = this.lang;
						 	   		model['_mls'] = function() { 
						 	   				return function(val) { 
						 	   					if (this[val]) {
						 	   						return this[val][lang];
						 	   					}
						 	   					return "";
						 	   				};
						 	   		};
					 	   		}
					 	   	};
					 	   	$.ajax( { type: "GET",
					 		   		 url: theme_url,
					 		   		 dataType: "text",
					 		   		 success: function(layoutTemplate, status,response) {
					 		   			function applyLayout(hostJson) {
					 		   				if (jsonHandler) {
					 		   					hostJson = jsonHandler(hostJson);
					 		   				}
					 		   				BennuPortal.addMls(hostJson);
					 		   				var new_body = Mustache.to_html(layoutTemplate,hostJson);
					 		   				var old_body = $('body').html();
					 		   				$('body').html(new_body);
					 		   				$("#content")[0].innerHTML = old_body;
					 		   				$("head").append('<link href="' + styles_url + '" rel="stylesheet">');
					 		   				$("body").show();
					 		   			}
					 		   			
					 		   			$.ajax( {type: "GET", 
					 		   					 url : json_handler_url,
					 		   					 dataType: "script",
					 		   					 }).done(function(script, textStatus) {
					 		   						 applyLayout(hostJson);
					 		   					 }).fail(function(jqxhr, settings, exception) {
					 		   						applyLayout(hostJson);
					 		   					});
					 		   		 }
					 	   	});
				 }
			});
		});
	});
})();