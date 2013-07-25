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
	
	var developmentMode = readCookie("developmentMode");

	$("body").hide();
	$.ajax({
		type: "GET",
		url: contextPath + "/api/bennu-portal/data",
		dataType: "json",
		success: function(hostJson, status, response) {
			var theme_base = contextPath + "/themes/" + hostJson.theme;
			var theme_url = theme_base + "/layout.html";
			var styles_url = theme_base + "/css/style.css";
			var json_handler_url = theme_base + "/js/jsonHandler.js";

			BennuPortal = {
				locales: hostJson.locales,
				locale: hostJson.locale,
				lang: (function(locale) {
					if (locale.indexOf("-") != -1) {
						return locale.split("-")[0];
					}
					return locale;
				})(hostJson.locale.tag),
				addMls: function(model) {
					var lang = this.locale.tag;
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
				
				login: function(user, pass, callback) {
					$.post(contextPath + "/api/bennu-core/profile/login", {
						username: user,
						password: pass
					}, function(data, textStatus, jqxhr) {
						callback(data, textStatus, jqxhr);
					});
				},

				logout: function(callback) {
					var logoutUrl = contextPath + "/api/bennu-core/profile/logout";
					if (hostJson.casEnabled) {
						logoutUrl = hostJson.logoutUrl;
					}
					$.get(logoutUrl, null, function(data,textStatus,jqxhr) {
						callback(data, textStatus, jqxhr);
					});
				},

				changeLanguage: function(tag, callback) {
					$.post(contextPath + "/api/bennu-core/profile/locale/" + tag, null, function(data,textStatus,jqxhr) {
						callback(data,textStatus,jqxhr);
					});
				},

				load: function(scripts) {
					$(scripts).each(function(i, e) {
						$.getScript(contextPath + e, function() {
							console.log("Loading " + e);
						});
					});
				}
			};
			//LOADING THEMES STATIC I18N MESSAGES
			var theme_messages_url = theme_base + "/i18n/messages-" + BennuPortal.lang + ".json";
			$.ajax({
				type: "GET",
				url: theme_messages_url,
				dataType: "json",
				success: function(messagesJson, status, response) {
					console.log("Loaded " + theme_messages_url + " successfully!");
					hostJson["_i18n"] = function() {
						return function(val) {
							if (messagesJson[val]) {
								return messagesJson[val];
							} else {
								return "_i18n!!" + BennuPortal.lang + "!!" + val + "!!";
							}
						}
					}
					$.ajaxSetup({ cache: true });
					$.ajax({
						type: "GET",
						url: theme_url,
						dataType: "text",
						success: function(layoutTemplate, status, response) {
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
								if(developmentMode) {
									$("head").prepend("<link rel='stylesheet/less' type='text/css' href='"+theme_base+"/less/style.less' />");
									$("head").append("<script type='text/javascript' src='" + contextPath +"/bennu-portal/js/less.min.js" + "'></script>");

								} else {
									$("head").append('<link rel="stylesheet" href="' + styles_url + '" rel="stylesheet" />');
								}
								$("body").show();
								$.ajaxSetup({ cache: false });
							}

							$.ajax({
								type: "GET",
								url: json_handler_url,
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
		}
	});
})();
