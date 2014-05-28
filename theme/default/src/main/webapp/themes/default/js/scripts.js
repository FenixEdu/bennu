$(function() {
        $("input[name=password]").keypress(function(e) {
                if (e.which == 13) {
                        e.preventDefault();
                        var user = $("input[name=username]").val(), pass = $(this).val();
                        $.post(window.contextPath + "/api/bennu-core/profile/login", { username: user, password: pass }, function() {
                                location.reload();
                        });
                } else { return true; }
        });
        $("#logout").click(function(e) {
                e.preventDefault();
                $.get(window.contextPath + "/api/bennu-core/profile/logout", null, function() {
                        location.reload();
                });
        });
        $(".lang").click(function(e) {
                if(e.target.parentElement.classList.contains("disabled")) { return; }
                var lang = $(e.target).data("lang").replace('_', '-');
                $.post(window.contextPath + "/api/bennu-core/profile/locale/" + lang, null, function() {
                        location.reload();
                });
        });
});
