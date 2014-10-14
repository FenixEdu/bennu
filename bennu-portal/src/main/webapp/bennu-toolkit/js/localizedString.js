(function () {
    Bennu.localizedString = Bennu.localizedString || {};
    Bennu.localizedString.attr = "bennu-localized-string";

    Bennu.localizedString.changeData = function (locale, localeButton, inputField, widget) {
        widget = $(widget);
        var val = $(widget.data("related")).val();

        if (val === "") {
            $(widget.data("related")).val("{}");
            val = "{}";
        }
        var val = JSON.parse(val)[locale.tag]
        inputField.val(val || "");
        localeButton.html(locale.displayName || locale.tag);
        localeButton.data("locale", locale);
    };

    Bennu.localizedString.updateValueForLanguage = function (input,localeContainer,widget){
        var data = JSON.parse($(widget.data("related")).val());
        data[localeContainer.data("locale").tag] = input.val();
        $(widget.data("related")).val(JSON.stringify(data));
    };

    Bennu.localizedString.makeLocaleList = function(menu, widget, callback){
        for (var i = 0; i < Bennu.locales.length; i++) {
            var locale = Bennu.locales[i];
            var menuOption = $("<li><a href='#'>" + (locale.displayName || locale.tag) + "</a>");
            menuOption.data("locale", locale);
            menuOption.on("click", callback);
            menu.append(menuOption);
        }
    };

    Bennu.localizedString.createWidget = function(input) {
        input = $(input);

        var attr = input.attr("bennu-html-editor");
        if (attr !== null && attr !== undefined){
            return;
        }

        if (!$(input).data("input")) {

            if (input.prop("tagName") == "INPUT") {
                var widget = $('<div class="bennu-localized-string-input-group" >'+
                    '<div class="input-group"><input type="text" class="form-control bennu-localized-string-input">'+
                    '<div class="input-group-btn bennu-localized-string-group">'+
                    '<button type="button" class="hidden-sm hidden-xs btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown">'+
                    '<span class="bennu-localized-string-language"></span> <span class="caret"></span></button>'+
                    '<button type="button" class="visible-sm visible-xs btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown">'+
                    '<span class="glyphicon glyphicon-globe"></span> <span class="caret"></span></button>'+
                    '</button>'+
                    '<ul class="dropdown-menu bennu-localized-string-menu pull-right" role="menu"></ul></div></div>'+
                    '<p class="help-block"></p></div>');
            } else if (input.prop("tagName") == "TEXTAREA") {
                var widget = $('<div class="bennu-localized-string-textArea"><p><div class="btn-group bennu-localized-string-group"><button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown"><span class="bennu-localized-string-language"></span><span class="caret"></span></button><ul class="dropdown-menu bennu-localized-string-menu" role="menu"></ul></div></p><p><textarea class="form-control bennu-localized-string-textarea"></textarea><p class="help-block"></p></div>');
            }

            widget.data("related", input);
            Bennu.localizedString.makeLocaleList($(".bennu-localized-string-menu", widget),widget, function (e) {
                Bennu.localizedString.changeData($(e.target).parent().data("locale"), $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget);
            });

            Bennu.localizedString.changeData(Bennu.locale, $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget);
            $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget).on("input propertychange", function () {

                Bennu.localizedString.updateValueForLanguage($(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), $(".bennu-localized-string-language", widget), widget);

                $(".help-block", widget).empty();
                widget.removeClass("has-error");
            });

            input.data("input", widget);
            input.after(widget);
            Bennu.validation.attachToForm(widget);

            return widget;
        }
    };

    $(function () {
        $("[bennu-localized-string]").map(function(i,e){
            Bennu.localizedString.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.localizedString.attr, {
            add: function(e){
                Bennu.localizedString.createWidget(e);
            },
            remove: function(e){
                if ($(e).attr(Bennu.localizedString.attr) === ""){
                    $(e).data("input").remove();
                }
            }
        });
    });
})();
