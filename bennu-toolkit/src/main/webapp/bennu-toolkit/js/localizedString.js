/*
 * localizedString.js
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
    Bennu.localizedString = Bennu.localizedString || {};
    Bennu.localizedString.attr = "bennu-localized-string";
    Bennu.localizedString.inputValidAttr = ["readonly", "placeholder", "maxlength","disabled"]
    Bennu.localizedString.textAreaValidAttr = ["readonly", "placeholder", "maxlength","disabled"]
    
    function getSingleTag(locale){
        return locale.tag.split("-")[0].toUpperCase();
    }

    function setLocale(locale,localeButton){
        localeButton.html(locale.displayName || getSingleTag(locale));
        localeButton.data("locale", locale);
    }

    function setTagButton(locale, tagButton){
        if(tagButton && tagButton.length) { tagButton.html(getSingleTag(locale)) };
    }

    Bennu.localizedString.changeData = function (locale, localeButton, inputField, widget, tagButton) {
        widget = $(widget);
        var val = $(widget.data("related")).val();

        if (val === "") {
            $(widget.data("related")).val("{}");
            val = "{}";
        }

        var val = JSON.parse(val)
        var tag = locale.tag;
        
        if (!(tag in val)){
            singleTag = getSingleTag(locale).toLowerCase();
            if (singleTag in val){
                tag = singleTag;
            }
        }

        val = val[tag]
        if (val !== inputField.val() || locale !== widget.data("locale")){
            if (inputField instanceof $ && inputField.hasClass('bennu-localized-string-input'))
                inputField.val($('<div>').html( val ).text() || "");
            else
                inputField.val(val || "");
            
            setLocale(locale,localeButton);
            setTagButton(locale, tagButton);
            
            widget.data("locale", locale);
        }
    };

    Bennu.localizedString.updateValueForLanguage = function (input, localeContainer, widget) {
        var data = JSON.parse($(widget.data("related")).val());
        var locale = localeContainer.data("locale")
        var tag = locale.tag

        if (!(tag in data)){
            var singleTag = getSingleTag(locale).toLowerCase();
            if (singleTag in data){
                tag = singleTag;
            }
        }

        data[tag] = input.val();
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

    Bennu.localizedString.getContent = function (input, locale, exactMatch) {
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
        return exactMatch ? null : input[Object.keys(input)[0]];
    };

    Bennu.localizedString.createWidget = function (input) {
        input = $(input);

        if (Bennu.utils.hasAttr(input,"bennu-html-editor")) {
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
            Bennu.utils.replaceRequired(input);


            if (input.prop("tagName") == "INPUT") {
                var widgetInput = $('.bennu-localized-string-input',widget);
                Bennu.utils.updateAttrs(input,widgetInput, Bennu.localizedString.inputValidAttr);
            } else if (input.prop("tagName") == "TEXTAREA") {
                var widgetInput = $('.bennu-localized-string-textarea', widget);
                Bennu.utils.updateAttrs(input,widgetInput, Bennu.localizedString.textAreaValidAttr);
            }
            
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
