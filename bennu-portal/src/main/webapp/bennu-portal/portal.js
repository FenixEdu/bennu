(function() {
	var root = this;
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
			hostJson.themePath = theme_base;
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
                        	if(typeof val === "string") {
                        		val = this[val];
                        	}
                            if (val) {
                                if (val[completeLanguage]) {
                                    return val[completeLanguage];
                                }
                                currentLanguage = BennuPortal.lang;
                                if (val[currentLanguage]) {
                                    return val[currentLanguage];
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
                                if (fallbackLanguage != undefined && val[fallbackLanguage] != undefined) {
                                    return val[fallbackLanguage];
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
						});
					});
				}
			};
			$.ajaxSetup({ cache: true });
			var messagesJson = {};
			$.getJSON(theme_base + "/i18n/messages-" + BennuPortal.lang + ".json").done(function (data) {
				messagesJson = data;
			}).
			always(function(data) {
			$.when(
				$.ajax(theme_url),
				$.getScript(contextPath + "/bennu-portal/js/twig.min.js"),
				$.getScript(json_handler_url)
			).always(function (layoutTemplate) {
				hostJson.contextPath = contextPath;
				BennuPortal.addMls(hostJson);

				if (typeof jsonHandler === "function") {
					hostJson = jsonHandler(hostJson);
				}
				var myTwig = Twig.exports || Twig;
				myTwig.extendFilter('i18n', function(val) {
					if(messagesJson[val]) {
						return messagesJson[val];
					} else {
						return "_i18n!!" + BennuPortal.lang + "!!" + val + "!!";
					}
					return messagesJson[value];
				});
				myTwig.extendFilter('mls', function (val) {
					return hostJson._mls()(val);
				});
				var template = myTwig.twig({ data: layoutTemplate[0] });
				var new_body = template.render(hostJson);
				$('body').append(new_body);
				$("#portal-container").appendTo("#content");
				if(developmentMode) {
					$.get(theme_base + '/less/style.less', function(data) {
						$("head").prepend("<link rel='stylesheet/less' type='text/css' href='"+theme_base+"/less/style.less' />");
						$("head").append("<script type='text/javascript' src='" + contextPath +"/bennu-portal/js/less.min.js" + "'></script>");
					}).fail(function() {
						if (window.console) {
							console.log("In development mode mapping the theme less directory in jetty or tomcat would enable less compilation")
						}
						$("head").append('<link rel="stylesheet" href="' + styles_url + '" rel="stylesheet" />');
					});
				} else {
					$("head").append('<link rel="stylesheet" href="' + styles_url + '" rel="stylesheet" />');
				}
				$('body').show();
			})
			});
		}
	});
}).call(this);
