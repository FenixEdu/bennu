(function() {
window.onhashchange = function() {
                var path = location.href.replace(location.href.substring(0, location.href.indexOf(contextPath) + contextPath.length + 1), "");
                var selected = false;
                var menuId = $("li.menu.active").attr("menu");
                $("ul[menu=\"" + menuId + "\"] li a").each(function(){
                                if(this.href.indexOf(path) != -1) {
                                        selected = this;
                                        return false;
                                }
                        });
                
                if(!selected) {
                        $("li a").each(function() {
                                if(this.href.indexOf(path) != -1) {
                                        selected = this;
                                        return false;
                                }
                        });
                }
                if (selected) {
                        var li = $(selected).parent()[0];
                        var ul = $(li).parent()[0];
                        
                        if ($(li).hasClass("menu"))        {
                                $("li.menu").each(function() {
                                        if (this === li) {
                                                $(this).addClass("active");
                                        }else {
                                                $(this).removeClass("active");
                                        }
                                });
                                
                                var menuId = $(li).attr("menu");
                                $("ul[menu]").each(function() {
                                        if ($(this).attr('menu') === menuId) {
                                                $(this).removeClass("hide");
                                        } else {
                                                $(this).addClass("hide");
                                        }
                                });
                                
                                $("li.submenu").each(function() {
                                                $(this).removeClass("active");
                                });
                        }
                        
                        if ($(li).hasClass("submenu"))        {
                                
                                $("li.submenu").each(function() {
                                        if (this === li) {
                                                $(this).addClass("active");
                                        }else {
                                                $(this).removeClass("active");
                                        }
                                });
                                
                                var menuId = $(ul).attr("menu");
                                $("li.menu").each(function() {
                                        if ($(this).attr("menu") === menuId) {
                                                $(this).addClass("active");
                                        }else {
                                                $(this).removeClass("active");
                                        }
                                });
                        }
                }
        };
})();