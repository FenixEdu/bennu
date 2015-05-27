(function () {
    var POST_ON_ERROR = false;
    Bennu.validation = {};

    Bennu.validation.attachToForm = function (widget) {
        var form = widget.closest("form");
        if (!form.data("bennu-validator")) {
            form.on("submit", function (submitEvent) {
                try {
                    var val = $("[bennu-localized-string],[bennu-html-editor]", form).map(function (i, xx) {
                        xx = $(xx);
                        xx.data("input").removeClass("has-error");
                        return Bennu.validation.validateInput(xx);
                    });

                    if (!Array.prototype.reduce.apply(val, [function (x, y) {
                        return x && y;
                    }, true])) {
                        submitEvent.preventDefault();
                        return false;
                    }
                } catch (e) {
                    console.error(e.stack);
                    return POST_ON_ERROR;
                }
            });

            form.data("bennu-validator", true);
        }
    };

    Bennu.validation.validateInput = function (inputObject) {
        var value = inputObject.val() ? JSON.parse(inputObject.val()) : null;
        if (inputObject.attr("required") != null || inputObject.attr("required") != undefined) {
            var val = Bennu.locales.map(function (x) {
                return Bennu.localizedString.getContent(value, x, true) || false;
            }).reduce(function (x, y) {
                return x && y;
            }, true);

            if (!val) {
                inputObject.data("input").addClass("has-error");
                $(".help-block", inputObject.data("input")).html('You need to to insert text in all languages');
            }

            return val;
        } else if (inputObject.attr("required-any") != null || inputObject.attr("required-any") != undefined) {
            var val = Bennu.locales.map(function (x) {
                return Bennu.localizedString.getContent(value, x, true) || false;
            }).reduce(function (x, y) {
                return x || y;
            }, false);
            if (!val) {
                inputObject.data("input").addClass("has-error");
                $(".help-block", inputObject.data("input")).html('You need to to insert text in at least one language');
            }

            return val;
        }
    };

})();
