$(function() {
        $(".lang").click(function(e) {
                if(e.target.parentElement.classList.contains("disabled")) { return; }
                var lang = $(e.target).data("lang").replace('_', '-');
                $.post(window.contextPath + "/api/bennu-core/profile/locale/" + lang, null, function() {
                        location.reload();
                });
        });
});
