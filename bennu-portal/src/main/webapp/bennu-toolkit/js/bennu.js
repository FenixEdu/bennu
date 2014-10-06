(function () {

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

    window.Bennu = function () {
        return $.apply($, arguments);
    };

    Bennu.version = "1.0.0";

    Bennu.toString = function () {
        return "Bennu Toolkit v" + Bennu.version;
    };

    Bennu.gensym = function () {
        var text = "";
        var possible = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (var i = 0; i < 5; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }

        return text;
    };

    Bennu.contextPath = readCookie("contextPath") || "";

    Bennu.developmentMode = (readCookie("developmentMode") && true) || false;

    function prefixForEvent(a) {
        a[0] = a[0].split(" ").map(function (e) {
            return "bennu-toolkit-" + e;
        }).reduce(function (x, y) {
            return x + " " + y;
        });
    }

    Bennu.on = function () {
        prefixForEvent(arguments);
        var q = $("body");
        q.on.apply(q, arguments);
    };

    Bennu.off = function () {
        prefixForEvent(arguments);
        var q = $("body");
        q.off.apply(q, arguments);
    };

    Bennu.trigger = function () {
        prefixForEvent(arguments);
        var q = $("body");
        q.trigger.apply(q, arguments);
    };

    Bennu.addMls = function (model) {
        var completeLanguage = this.locale.tag;
        var currentLanguage = completeLanguage;
        var langs = this.locales;
        model['_mls'] = function () {
            return function (val) {
                if (typeof val === "string") {
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
                    for (var lang in val) {
                        if (lang.indexOf(currentLanguage) === 0) {
                            return val[lang];
                        }
                    }
                    var fallbackLanguage = undefined;
                    $(langs).each(function () {
                        var eachlang = this.tag;
                        if (eachlang != completeLanguage && eachlang.indexOf(currentLanguage) === 0) {
                            fallbackLanguage = eachlang;
                            return false;
                        }
                    });
                    if (fallbackLanguage != undefined && val[fallbackLanguage] != undefined) {
                        return val[fallbackLanguage];
                    }
                    // Fallback, return the first key in the object...
                    return val[Object.keys(val)[0]];
                }
                return "_mls!!" + val + "!!";
            };
        };
        model["_lang"] = currentLanguage;
    };

    Bennu.login = function (user, pass, callback) {
        $.post(Bennu.contextPath + "/api/bennu-core/profile/login", {
            username: user,
            password: pass
        }, function (data, textStatus, jqxhr) {
            callback(data, textStatus, jqxhr);
        });
    };

    Bennu.logout = function (callback) {
        var logoutUrl = Bennu.contextPath + "/api/bennu-core/profile/logout";
        if (hostJson.casEnabled) {
            logoutUrl = hostJson.logoutUrl;
        }
        $.get(logoutUrl, null, function (data, textStatus, jqxhr) {
            callback(data, textStatus, jqxhr);
        });
    };

    Bennu.changeLanguage = function (tag, callback) {
        $.post(Bennu.contextPath + "/api/bennu-core/profile/locale/" + tag, null, function (data, textStatus, jqxhr) {
            callback(data, textStatus, jqxhr);
        });
    };

    Bennu.load = function (scripts) {
        $(scripts).each(function (i, e) {
            $.getScript(Bennu.contextPath + e, function () {
            });
        });
    };

    Bennu.monitor = Bennu.monitor || {};

    var target = document.querySelector('body');

    Bennu.monitor.checkFor = function (tag, actions) {
        var observer = new MutationObserver(function (mutations) {

        if ($.grep(mutations, function (e) {
                return $.grep(e.addedNodes, function (e) {
                    var contains = $("[" + tag + "]", $(e)).length !== 0;
                    var is = $(e).attr(tag) === "";
                    return is || contains;
                }).length !== 0;
            }).length !== 0) {
                actions.add && actions.add(e);
            }

            $.map(mutations, function (e) {
                return $.map(e.removedNodes, function (e) {
                    actions.remove && actions.remove(e);
                });
            });
        });

        var config = {
            attributes: true,
            subtree: true,
            childList: true,
            characterData: true
        };
        observer.observe(target, config);
    };

    //TODO: this needs to go, prefering head meta keys;
    $.ajax({
        type: "GET",
        url: Bennu.contextPath + "/api/bennu-portal/data",
        async: false,
        dataType: "json",
        success: function (hostJson, status, response) {
            var theme_base = Bennu.contextPath + "/themes/" + hostJson.theme;
            hostJson.themePath = theme_base;
            var theme_url = theme_base + "/layout.html";
            var styles_url = theme_base + "/css/style.css";
            var json_handler_url = theme_base + "/js/jsonHandler.js";

            Bennu.username = hostJson.username;
            Bennu.locales = hostJson.locales;
            Bennu.locale = hostJson.locale;
            Bennu.groups = hostJson.groups;
            Bennu.lang = (function (locale) {
                if (locale.indexOf("-") != -1) {
                    return locale.split("-")[0];
                }
                return locale;
            })(hostJson.locale.tag);
        }
    });
}).call(this);
