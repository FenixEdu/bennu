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
        var val = obj.attr(attr);
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
