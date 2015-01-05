(function () {
    Bennu.localizedString = Bennu.localizedString || {};
    Bennu.localizedString.attr = "bennu-localized-string";

    function getSingleTag(locale){
        return locale.tag.split("-")[0].toUpperCase();
    }

    Bennu.localizedString.changeData = function (locale, localeButton, inputField, widget, tagButton) {
        widget = $(widget);
        var val = $(widget.data("related")).val();

        if (val === "") {
            $(widget.data("related")).val("{}");
            val = "{}";
        }
        var val = JSON.parse(val)[locale.tag]
        if (val !== inputField.val()){
            inputField.val(val || "");
            
            localeButton.html(locale.displayName || getSingleTag(locale));
            if(tagButton && tagButton.length) { tagButton.html(getSingleTag(locale)) };

            localeButton.data("locale", locale);
            
            widget.data("locale", locale);
        }
    };

    Bennu.localizedString.updateValueForLanguage = function (input, localeContainer, widget) {
        var data = JSON.parse($(widget.data("related")).val());
        data[localeContainer.data("locale").tag] = input.val();
        $(widget.data("related")).val(JSON.stringify(data));
        $(widget.data("related")).trigger("change");
    };

    Bennu.localizedString.makeLocaleList = function (menu, widget, callback) {
        for (var i = 0; i < Bennu.locales.length; i++) {
            var locale = Bennu.locales[i];
            var menuOption = $("<li><a href='#' onclick='return false;' >" + (locale.displayName || locale.tag) + "</a>");
            menuOption.data("locale", locale);
            menuOption.on("click", callback);
            menu.append(menuOption);
        }
    };

    Bennu.localizedString.getContent = function (input, locale) {
        if (!input || typeof input === 'string') return input;

        if (locale && typeof locale !== "string"){
            var tag = locale.tag;
        }else if(!locale){
            var tag = Bennu.locale.tag;
        }else{
            var tag = locale
        }

        if (tag in input) {
            return input[tag];
        }
        tag = tag.split('-')[0];
        if (tag in input) {
            return input[tag];
        }
        for (var lang in input) {
            if (lang.indexOf(tag) === 0) {
                return input[lang];
            }
        }
        return input[Object.keys(input)[0]];
    };

    Bennu.localizedString.createWidget = function (input) {
        input = $(input);

        var attr = input.attr("bennu-html-editor");
        if (attr !== null && attr !== undefined) {
            return;
        }

        if (!$(input).data("input")) {

            if (input.prop("tagName") == "INPUT") {
                var widget = $('<div class="bennu-localized-string-input-group" >' +
                    '<div class="input-group"><input type="text" class="form-control bennu-localized-string-input">' +
                    '<div class="input-group-btn bennu-localized-string-group">' +
                    '<button type="button" class=" btn btn-default dropdown-toggle bennu-localized-string-button-full bennu-localized-string-button" data-toggle="dropdown">' +
                    '<span class="bennu-localized-string-language"></span> <span class="caret"></span></button>' +
                    '<button type="button" class=" btn btn-default dropdown-toggle bennu-localized-string-button-short bennu-localized-string-button" data-toggle="dropdown">' +
                    '<span class="bennu-localized-string-tag"></span> <span class="caret"></span></button>' +
                    '</button>' +
                    '<ul class="dropdown-menu bennu-localized-string-menu pull-right" role="menu"></ul></div></div>' +
                    '<p class="help-block"></p></div>');
            } else if (input.prop("tagName") == "TEXTAREA") {
                var widget = $('<div class="bennu-localized-string-textArea"><p><div class="btn-group bennu-localized-string-group"><button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown"><span class="bennu-localized-string-language"></span><span class="caret"></span></button><ul class="dropdown-menu bennu-localized-string-menu" role="menu"></ul></div></p><p><textarea class="form-control bennu-localized-string-textarea"></textarea><p class="help-block"></p></div>');
            }

            widget.data("related", input);
            Bennu.localizedString.makeLocaleList($(".bennu-localized-string-menu", widget), widget, function (e) {
                Bennu.localizedString.changeData($(e.target).parent().data("locale"), $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget, $(".bennu-localized-string-tag", widget));
            });

            Bennu.localizedString.changeData(Bennu.locale, $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget, $(".bennu-localized-string-tag", widget));
            $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget).on("input propertychange", function () {
                Bennu.localizedString.updateValueForLanguage($(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), $(".bennu-localized-string-language", widget), widget);

                $(".help-block", widget).empty();
                widget.removeClass("has-error");
            });

            function nextLocale() {
                var idx = localeIndex($(widget).data('locale')) + 1;
                if(idx == Bennu.locales.length) { idx = 0; }
                return Bennu.locales[idx];
            };

            function previousLocale() {
                var idx = localeIndex($(widget).data('locale')) - 1;
                if(idx < 0) { idx = Bennu.locales.length - 1; }
                return Bennu.locales[idx];
            };

            function localeIndex(locale) {
                var idx = -1;
                $.map(Bennu.locales, function(val, i) {
                    if(val.tag == locale.tag) { idx = i; }
                });
                return idx;
            };

            var modOn = false;
            widget.keydown(function(evt) {
                if(evt.keyCode == 18) {
                    modOn = true;
                } else if(modOn) {
                    var newLocale;
                    if(evt.keyCode == 40) { newLocale = nextLocale(); }
                    else if(evt.keyCode == 38) { newLocale = previousLocale(); }
                    if(newLocale) {
                        Bennu.localizedString.changeData(newLocale, $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget, $(".bennu-localized-string-tag", widget));
                    }
                }
            });

            widget.keyup(function (evt) {
                if(evt.keyCode == 18) { modOn = false; }
            });

            input.data("input", widget);
            input.on("change.bennu", function (e) {

                Bennu.localizedString.changeData(widget.data("locale"), $(".bennu-localized-string-language", widget), $(".bennu-localized-string-input,.bennu-localized-string-textarea", widget), widget, $(".bennu-localized-string-tag", widget));
                input.data("handler").trigger();
            });

            input.after(widget);
            Bennu.validation.attachToForm(widget);
            recalculateButtons(widget);
            return Bennu.widgetHandler.makeFor(input);
        }
    };

    function recalculateButtons(el) {
        el = el || $(".bennu-localized-string-input-group");
        el.map(function(i, e) { 
            e = $(e); 
            var z = $(".bennu-localized-string-button-full", e)
            var x = $(".bennu-localized-string-button-short", e)
            if(x.length && z.length){
                if ((z.width() / e.width()) > 1/3){
                    if (z[0].style.display !== "none"){
                        x[0].style.display = "inline";
                        z[0].style.display = "none";
                    }
                }else{
                    if (x[0].style.display !== "none"){
                        z[0].style.display = "inline";
                        x[0].style.display = "none";
                    }
                }
            }
        });
    }

    $( window ).resize(function(){

        recalculateButtons();
    });
})();
