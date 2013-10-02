(function() {
	var root = this;
	var cookies = null;
	console.log("run portal");
	
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

	var contextPath = readCookie("contextPath") || "";
	window.contextPath = contextPath;
	
	var developmentMode = readCookie("developmentMode");
	
	$.ajax({
		type: "GET",
		url: contextPath + "/api/bennu-portal/data",
		async: false,
		dataType: "json",
		success: function(hostJson, status, response) {
			var theme_base = contextPath + "/themes/" + hostJson.theme;
			var theme_url = theme_base + "/layout.html";
			var styles_url = theme_base + "/css/style.css";
			var json_handler_url = theme_base + "/js/jsonHandler.js";

			root.BennuPortal = {
				username: hostJson.username,
				locales: hostJson.locales,
				locale: hostJson.locale,
				groups: hostJson.groups,
				lang: (function(locale) {
					if (locale.indexOf("-") != -1) {
						return locale.split("-")[0];
					}
					return locale;
				})(hostJson.locale.tag),
				addMls: function(model) {
                    var completeLanguage = this.locale.tag;
                    var currentLanguage = completeLanguage;
                    var langs = this.locales;
                    model['_mls'] = function() {
                        return function(val) {
                            if (this[val]) {
                                if (this[val][completeLanguage]) {
                                    return this[val][completeLanguage];
                                }
                                currentLanguage = BennuPortal.lang;
                                if (this[val][currentLanguage]) {
                                    return this[val][currentLanguage];
                                }
                                
                                //search for other specific currentLanguage
                                var fallbackLanguage = undefined;
                                $(langs).each(function() {
                                    var eachlang = this.tag;
                                    if (eachlang != completeLanguage && eachlang.indexOf(currentLanguage) === 0) {
                                        fallbackLanguage = eachlang;
                                        return false;
                                    }
                                });
                                if (fallbackLanguage != undefined && this[val][fallbackLanguage] != undefined) {
                                    return this[val][fallbackLanguage];
                                }
                            }
                            return "_mls!!" + val + "!!";
                        };
                    };
                    model["_lang"] = currentLanguage;
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
								$('body').append(new_body);
								$("#portal-container").appendTo("#content");
								$('body').show();
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
								async: false,
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
}).call(this);
