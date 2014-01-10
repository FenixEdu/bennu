(function() {
        $(".lang").click(function(e) {
                var lang = $(e.target).data("lang");
                BennuPortal.changeLanguage(lang, function() {
                        location.reload();
                });
        });
})()