(function () {
    var POST_ON_ERROR = false;
    Bennu.validation = {};

    Bennu.validation.attachToForm = function (widget) {
        var form = widget.closest("form");
        if (!form.data("bennu-validator")) {
            form.on("submit", function (submitEvent) {
                try {
                    var val = Array.prototype.reduce.apply([
                    $("[bennu-localized-string]", form).map(function (i, xx) {
                        xx = $(xx);
                        xx.data("input").removeClass("has-error");
                        return Bennu.validation.validateLocalizedInput(xx);
                    }), 

                    $("[bennu-time],[bennu-date],[bennu-datetime]", form).map(function (i, xx) {
                        xx = $(xx);
                        xx.data("input").removeClass("has-error");
                        return Bennu.validation.validateDateTime(xx);
                    }),

                    $("[bennu-html-editor]", form).map(function (i, xx) {
                        xx = $(xx);

                        if(Bennu.utils.hasAttr(xx, "bennu-localized-string")){
                            // the other handler will check it;
                            return true;
                        }

                        xx.data("input").removeClass("has-error");
                        return Bennu.validation.validateInput(xx);
                    })], [$.merge, []]);

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

    var messages = {
        'bennu-date':'You need to select one date',
        'bennu-time':'You need to select a time',
        'bennu-date-time':'You need to select a date and time'
    }

    Bennu.validation.validateDateTime = function(inputObject){
        var value = inputObject.val();
        var val = true;
        if (Bennu.utils.hasAttr(xx, "bennu-required")) {
            if (!value) {
                inputObject.data("input").addClass("has-error");

                if (Bennu.utils.hasAttr(inputObject, "bennu-time")) {
                    $(".help-block", inputObject.data("input")).html(messages['bennu-time']);
                }

                if (Bennu.utils.hasAttr(inputObject, "bennu-date")) {
                    $(".help-block", inputObject.data("input")).html(messages['bennu-date']);
                }

                if (Bennu.utils.hasAttr(inputObject, "bennu-datetime")) {
                    $(".help-block", inputObject.data("input")).html(messages['bennu-date-time']);
                }
                val = false;
            }
        }

        return val;
    }

    Bennu.validation.validateInput = function (inputObject) {
        var value = inputObject.val();
        var val = true;
        if (Bennu.utils.hasAttr(inputObject, "bennu-required")) {
            if (!value) {
                inputObject.data("input").addClass("has-error");
                $(".help-block", inputObject.data("input")).html('This field is required');
                val = false;
            }
        }
        return val;
    };

    Bennu.validation.validateLocalizedInput = function (inputObject) {
        var value = inputObject.val() ? JSON.parse(inputObject.val()) : null;
        if (Bennu.utils.hasAttr(inputObject, "bennu-required")) {
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
        } else if (Bennu.utils.hasAttr(inputObject, "required-any")) {
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
