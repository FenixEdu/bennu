$(document).ready(function(){  
    $("ul.admin-bar-top li.contains-submenu").hover(function() {
  
        $(this).find("ul.admin-bar-sub").slideDown('fast').show();
  
        $(this).hover(function() {  
        }, function(){  
            $(this).find("ul.admin-bar-sub").slideUp('fast');
        });  
  
        });
});