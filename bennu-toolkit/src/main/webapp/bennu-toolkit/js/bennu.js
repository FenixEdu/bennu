/*! 
 * Bennu Toolkit
 * https://github.com/FenixEdu/bennu
 * Copyright (c) 2014 Instituto Superior Técnico
 */
/*
 * bennu.js
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

    window.Bennu = window.Bennu || {};
    
    $(function(){
	Bennu.loaded = true;
    })

    Bennu.version = "${project.version}";

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

    Bennu.contextPath = window.contextPath;
    if(!Bennu.contextPath) {
        $("script").map(function (idx, el) {
            var src = el.getAttribute('src');
            if(src && src.indexOf('/bennu-toolkit/js/toolkit.js') != -1) {
                Bennu.contextPath = src.substring(0, src.indexOf('/bennu-toolkit/js/toolkit.js'));
            }
        });
    }

    Bennu.contextPath = Bennu.contextPath || "";

    function prefixForEvent(a) {
        a[0] = a[0].split(" ").map(function (e) {
            return "bennu-toolkit-" + e;
        }).reduce(function (x, y) {
            return x + " " + y;
        });
    }

    Bennu.on = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.on.apply(q, arguments);
    };

    Bennu.off = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.off.apply(q, arguments);
    };

    Bennu.trigger = function () {
        prefixForEvent(arguments);
        var q = $("html");
        q.trigger.apply(q, arguments);
    };

    // Utils
    // ---------------------------------------------

    Bennu.utils =  Bennu.utils || {};

    Bennu.utils.uniqueArray = function(arr){
       var u = {}, a = [];
       for(var i = 0, l = arr.length; i < l; ++i){
          if(u.hasOwnProperty(arr[i])) {
             continue;
          }
          a.push(arr[i]);
          u[arr[i]] = 1;
       }
       return a;
    };

    Bennu.utils.updateAttrs = function(input, widgetInput, allowedAttrs){
        input = $(input);

        var cache = widgetInput.data("attrCache");

        if (cache){
            // If the attribute was removed after attaching to dom;
            for (var i = 0; i < cache.length; i++) {
                var attr = cache[i];
                if (Bennu.utils.hasAttr(input, attr)){
                    widgetInput.removeAttr(attr);
                }
            };
        }

        cache = [];

        $.each(input[0].attributes, function(i, attrib){
            if (!(allowedAttrs.indexOf(attrib.name) < 0)){
                widgetInput.attr(attrib.name,attrib.value);
                cache.push(attrib.name);
            }
        });

        widgetInput.data("attrCache", cache)        
    }

    Bennu.utils.hasAttr = function(obj,attr){
        var val = $(obj).attr(attr);
        return (typeof val !== typeof undefined && val !== false);
    }

    Bennu.utils.replaceRequired = function(input){
        input = $(input);

        if (Bennu.utils.hasAttr(input,'required')) {
            input.removeAttr("required");
            input.attr("bennu-required","required");
        }
    }


    Bennu.widgetHandler = Bennu.widgetHandler || {}
    Bennu.widgetHandler.makeFor = function (e, onremove){
        var events = [];
        var result = {
            get:function(){
                return $(e).val();
            },

            set:function(val){
                $(e).val(val);
                $(e).trigger("change");
            },

            clear:function(val){
                this.set("");
            },

            onchange:function(fn){
                events.push(fn);
            },

            trigger: function(){
                for (var i = 0; i < events.length; i++) {
                    events[i].apply(e,{
                        type:"change",
                        target:e
                    });
                }
            },

            remove: function(){
                $(e).data("input").remove();
                $(e).data("input", null);
                $(e).data("handler", null);
                $(e).off(".bennu");
            }
        }

        $(e).data("handler", result);
        return result;
    };
    var _start = function(){
	if (Bennu.loaded){
	   Bennu.trigger("load");
	}else{
	   $(function (){
    		Bennu.trigger("load");
           });
	}            	   
    }
    if(Bennu.locales) {
	  _start();
    } else {
        $.ajax({
            type: "GET",
            url: Bennu.contextPath + "/api/bennu-portal/data",
            async: false,
            dataType: "json",
            success: function (hostJson, status, response) {
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
		
                _start();
            },
            error: function (xhr, status, errorThrown) {
                Bennu.username = null;
                Bennu.locales = [
                    {displayName: "English (United Kingdom)", lang: "en", tag: "en-GB"}
                ];
                Bennu.locale = {displayName: "English (United Kingdom)", lang: "en", tag: "en-GB"};
                Bennu.groups = null;
                Bennu.lang = "en";
                _start();

            }
        })
    };
}).call(this);
