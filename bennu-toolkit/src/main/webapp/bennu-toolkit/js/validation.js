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

                        if(xx.attr("bennu-localized-string") != null || xx.attr("bennu-localized-string") != undefined){
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
        if (inputObject.attr("bennu-required") != null || inputObject.attr("bennu-required") != undefined) {
            if (!value) {
                inputObject.data("input").addClass("has-error");

                var attr = inputObject.attr('bennu-time');
                if (typeof attr !== typeof undefined && attr !== false) {
                    $(".help-block", inputObject.data("input")).html(messages['bennu-time']);
                }

                attr = inputObject.attr('bennu-date');
                if (typeof attr !== typeof undefined && attr !== false) {
                    $(".help-block", inputObject.data("input")).html(messages['bennu-date']);
                }

                attr = inputObject.attr('bennu-datetime');
                if (typeof attr !== typeof undefined && attr !== false) {
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
        if (inputObject.attr("bennu-required") != null || inputObject.attr("bennu-required") != undefined) {
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
        if (inputObject.attr("bennu-required") != null || inputObject.attr("bennu-required") != undefined) {
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
