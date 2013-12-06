(function() {
        $("input[name=password]").keypress(function(e) {
                if (e.which == 13) {
                        e.preventDefault();
                        var user = $("input[name=username]").val(), pass = $(this).val();
                        BennuPortal.login(user, pass, function() {
                                location.reload();
                        });
                } else {
                        return true;
                }
        });

        $("#logout").click(function(e) {
                e.preventDefault();
                BennuPortal.logout(function() {
                        location.reload();
                });
        });
}());