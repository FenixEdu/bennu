function loadScript(url, callback) {
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
	var cookies = null;
	function readCookie(name, c, C, i) {
		if (cookies) {
			return cookies[name];
		}

		c = document.cookie.split('; ');
		cookies = {};

		for (i = c.length - 1; i >= 0; i--) {
			C = c[i].split('=');
			cookies[C[0]] = C[1];
		}

		return cookies[name];
	}

	var contextPath = readCookie("contextPath") || "/";
	window.contextPath = contextPath;

	$("body").hide();
	$.ajax({
		type : "GET",
		url : contextPath + "/api/bennu-portal/hostmenu/"
				+ window.location.hostname,
		dataType : "json",
		success : function(hostJson, status, response) {
			var theme_base = contextPath + "/themes/" + hostJson.theme;
			var theme_url = theme_base + "/layout.html";
			var styles_url = theme_base + "/css/style.css";
			var json_handler_url = theme_base + "/js/jsonHandler.js";

			BennuPortal = {
				locales : hostJson.locales,
				locale : hostJson.locale,
				lang : (function(locale) {
					if (locale.indexOf("-") != -1) {
						return locale.split("-")[0];
					}
					return locale;
				})(hostJson.locale.tag),
				addMls : function(model) {
					var lang = this.lang;
					model['_mls'] = function() {
						return function(val) {
							if (this[val]) {
								return this[val][lang];
							}
							return "_mls!!" + val + "!!";
						};
					};
					model["_lang"] = lang;
				},

				load : function(scripts) {
					$(scripts).each(function(i, e) {
						$.getScript(contextPath + e, function() {
							console.log("loading " + e);
						});
					});
				}
			};
			$.ajax({
				type : "GET",
				url : theme_url,
				dataType : "text",
				success : function(layoutTemplate, status, response) {
					function applyLayout(hostJson) {
						hostJson.contextPath = contextPath;
						BennuPortal.addMls(hostJson);
						
						if (jsonHandler) {
							hostJson = jsonHandler(hostJson);
						}
						var new_body = Mustache.to_html(layoutTemplate,
								hostJson);
						var old_body = $('body').html();
						$('body').html(new_body);
						$("#content")[0].innerHTML = old_body;
						$("head").append('<link rel="stylesheet/less" href="less/bootstrap.less" />');
						$("head").append(
								'<link href="' + styles_url
										+ '" rel="stylesheet">');
4						$("head").append("<script type='text/javascript' src='" + contextPath + "/js/libs/less/less-min.js" + "'></script>");
						$("body").show();
					}

					$.ajax({
						type : "GET",
						url : json_handler_url,
						dataType : "script",
					}).done(function(script, textStatus) {
						applyLayout(hostJson);
					}).fail(function(jqxhr, settings, exception) {
						applyLayout(hostJson);
					});
				}
			});
		}
	});
})();