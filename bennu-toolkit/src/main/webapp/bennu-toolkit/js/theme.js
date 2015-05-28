(function () {

    Bennu.theme = Bennu.theme || {};
    var cache = {};
    function hackColor(cls){
    	if (!(cls in cache)){
	    	var a = $("<button style='display:none;' class='btn btn-" + cls + "'></button>"); 
	    	$(document.body).append(a); 
	    	var color = a.css("backgroundColor");
	    	a.remove();
	    	cache[cls] = color;
	    	return color;
    	}else{
    		return cache[cls];
    	}
    }

    function hackParagraph(){
        if (!("paragraph" in cache)){
            var a = $("<p style='display:none;'></p>"); 
            $(document.body).append(a); 
            var font = a.css("fontFamily");
            a.remove();
            cache["paragraph"] = font;
            return font;
        }else{
            return cache["paragraph"];
        }
    }

    function hackHeading(){
        if (!("heading" in cache)){
            var a = $("<h1 style='display:none;'></h1>"); 
            $(document.body).append(a); 
            var font = a.css("fontFamily");
            a.remove();
            cache["heading"] = font;
            return font;
        }else{
            return cache["heading"];
        }
    }

    Bennu.theme.hFontFamily = function(){
        return hackHeading();
    };

    Bennu.theme.pFontFamily = function(){
        return hackParagraph();
    };

    Bennu.theme.primary = function(){
    	return hackColor("primary");
    };

    Bennu.theme.success = function(){
    	return hackColor("success");
    };

    Bennu.theme.info = function(){
    	return hackColor("info");
    };

    Bennu.theme.warning = function(){
    	return hackColor("warning");
    };

    Bennu.theme.danger = function(){
    	return hackColor("danger");
    };

})();