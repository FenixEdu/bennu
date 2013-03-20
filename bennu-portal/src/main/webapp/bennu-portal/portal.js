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



function addMls(model) {
	model['_mls'] = function() { 
		return function(val) { 
			if (this[val]) {
				if (this[val].pt) {
					return this[val].pt;
				}
				if (this[val]["pt-PT"]) {
					return this[val]["pt-PT"];
				}
			}
			return "";
		};
	};
}
(function() {
	loadScript("bennu-portal/js/jquery.min.js", function() {
		$("body").hide();
		$.getScript("bennu-portal/js/mustache.min.js", function() {
			$.ajax( {type : "GET",
				 url : "../api/bennu-portal/hostmenu/" + window.location.hostname,
				 dataType: "json",
				 success : function(hostJson, status, response) {
					 		var theme_base = "/themes/" + hostJson.theme;
					 	   	var theme_url = theme_base + "/layout.html";
					 	   	var styles_url = theme_base + "/css/style.css";
					 	   	$.ajax( { type: "GET",
					 		   		 url: theme_url,
					 		   		 dataType: "text",
					 		   		 success: function(layoutTemplate, status,response) {
					 		   			addMls(hostJson);
					 		   			var new_body = Mustache.to_html(layoutTemplate,hostJson);
					 		   			var old_body = $('body').html();
					 		   			$('body').html(new_body);
					 		   			$("#content")[0].innerHTML = old_body;
					 		   			$("head").append('<link href="' + styles_url + '" rel="stylesheet">');
					 		   			$("body").show();
					 		   		 }
					 	   	});
				 }
			});
		});
	});
})();