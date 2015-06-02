/*
 * theme.js
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