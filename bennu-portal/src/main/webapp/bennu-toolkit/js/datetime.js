(function () {
    Bennu.datetime = Bennu.datetime || {};

    Bennu.datetime.createWidget = function (e) {
        if($(e).attr("bennu-date") === ""){
            Bennu.datetime.createDateWidget(e);
        }

        if($(e).attr("bennu-time") === ""){
            Bennu.datetime.createTimeWidget(e);
        }

        if($(e).attr("bennu-datetime") === ""){
            Bennu.datetime.createDateTimeWidget(e);
        }
    }

    Bennu.datetime.createDateWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span></span><input type="text" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() == "") {
            currentDate = new Date();
        } else {
            currentDate = new Date(currentDate);
        }

        var options = {
            defaultDate: currentDate,
            language: Bennu.lang,
            showToday: true,
            pickDate: true,
            pickTime: false,
        }

        if (e.attr("min-date") !== undefined) {
            options.minDate = new Date(e.attr("min-date"));
        }

        if (e.attr("max-date") === "") {
            options.maxDate = new Date(e.attr("max-date"));
        }

        if (e.attr("unavaible-dates") !== undefined) {
            var dates = e.attr("unavaible-dates").split(",")
            var result = [];
            for (var i = 0; i < dates.length; i++) {
                result.add(new Date(dates[i]));
            }
            options.disabledDates = result;
        }

        if (e.attr("avaible-dates") !== undefined) {
            var dates = e.attr("avaible-dates").split(",")
            var result = [];
            for (var i = 0; i < dates.length; i++) {
                result.add(new Date(dates[i]));
            }
            options.enabledDates = result;
        }

        $("input", widget).on("input propertychange", function (x) {
            x = $(x.target);
            e.val(x.val());
        }).datetimepicker(options);

        e.after(widget);
    }

    function parseTime(s){
        s = s.trim();
        if (s === ""){
            var parts = [0,0,0]
        }else{
            if (s.indexOf(".") < 0){
                s = s.split(".")[0];
            }

            var parts = s.split(":");
            parts[0] = parseInt(parts[0]);
            parts[1] = (parts[1] && parseInt(parts[1])) || 0;
            parts[2] = (parts[2] && parseInt(parts[2])) || 0;
        }

        var date = new Date();
        date.setHours(parts[0])
        date.setMinutes(parts[1])
        date.setSeconds(parts[2])
        return date;
    }

    Bennu.datetime.createTimeWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-time"></span></span><input type="text" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() == "") {
            currentDate = new Date();
        } else {
            currentDate = parseTime(currentDate);
        }

        var options = {
            defaultDate: currentDate,
            language: Bennu.lang,
            pickDate: false,
            pickTime: true,
            useSeconds: true
        }

        if (e.attr("only-hours") !== undefined) {
            options.useSeconds = false
            options.useMinutes = false
        }

        if (e.attr("no-seconds") !== undefined) {
            options.useSeconds = false
            options.useMinutes = true
        }

        if (e.attr("minute-stepping") !== undefined) {
            options.minuteStepping = parseInt(e.attr("minute-stepping"));
        }

        $("input", widget).on("input propertychange", function (x) {
            x = $(x.target);
            e.val(x.val());
        }).datetimepicker(options);

        e.after(widget);
    }

    Bennu.datetime.createDateTimeWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span></span><input type="text" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() == "") {
            currentDate = new Date();
        } else {
            currentDate = new Date(currentDate);
        }

        var options = {
            useSeconds: true,
            defaultDate: currentDate,
            sideBySide: true,
            pickDate: true,
            pickTime: true,
            useCurrent: false,
            pickTime: true,
            language: Bennu.lang
        };

        if (e.attr("select-date") === "") {
            options.pickDate = true;
            options.pickTime = false;
        }

        if (e.attr("select-time") === "") {
            options.pickDate = false;
            options.pickTime = true;
        }

        if (e.attr("select-datetime") === "") {
            options.pickDate = true;
            options.pickTime = true;
        }

        $(".bennu-datetime-input", widget).val();

        $("input", widget).on("input propertychange", function (x) {
            x = $(x.target);
            e.val(x.val());
        }).datetimepicker(options);

        e.after(widget);
        e.data("input")
        widget.data("related", e)
    }

    Bennu.on("load", function () {
        $("[bennu-datetime],[bennu-time],[bennu-date]").each(function (i, e) {
            Bennu.datetime.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.datetime.attr, {
            add: function (e) {
                Bennu.datetime.createWidget(e);
            },
            remove: function (e) {
                if ($(e).attr(Bennu.datetime.attr) === "") {
                    $(e).data("input").remove();
                }
            }
        });
    });
}());
