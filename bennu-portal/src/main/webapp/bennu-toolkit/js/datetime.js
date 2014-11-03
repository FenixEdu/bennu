(function () {

    function parseTime(s){
        s = s.trim();
        if (s === ""){
            var parts = [0,0,0]
        }else{
            if (s.indexOf(".") > 0){
                var t = s.split(".");
                s = t[0];
                var mili = parseInt(t[1]);
                console.log(s);
            }else{
                var mili = 000;
            }

            var parts = s.split(":");
            parts[0] = parseInt(parts[0]);
            parts[1] = (parts[1] && parseInt(parts[1])) || 0;
            parts[2] = (parts[2] && parseInt(parts[2])) || 0;
        }

        var date = new Date();
        date.setHours(parts[0]);
        date.setMinutes(parts[1]);
        date.setSeconds(parts[2]);
        date.setMilliseconds(mili);
        return date;
    }

    Bennu.datetime = Bennu.datetime || {};

    Bennu.datetime.createWidget = function (e) {
        var e = $(e);
        var datetime = (e.attr("bennu-datetime") === "");
        var date = (e.attr("bennu-date") === "");
        var time = (e.attr("bennu-time") === "");

        if(datetime || (date && time)){
            return Bennu.datetime.createDateTimeWidget(e);
        }

        if(date){
            return Bennu.datetime.createDateWidget(e);
        }

        if(time){
            return Bennu.datetime.createTimeWidget(e);
        }
    }
    function dateOptions(e, options){
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

        return 
    };

    Bennu.datetime.createDateWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span></span><input data-date-format="DD/MM/YYYY" type="text" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() === "") {
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
        };

        dateOptions(e, options);

        $("input", widget).on("change", function (x) {
            x = $(x.target);
            var r = moment(x.val(), "DD/MM/YYYY").format("YYYY-MM-DD");
            if (r !== e.val()){
                e.val(r);
                e.trigger("change");
            }
        }).datetimepicker(options);

        e.after(widget);

        e.on("change.bennu", function(ev){
            var data = $(e).val();

            if (data.trim() == "") {
                data = new Date();
            } else {
                data = new Date(data);
            }
            e.val(moment(data).format("YYYY-MM-DD"));

            var r = $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(moment(data).format("DD/MM/YYYY"))
            var t = moment(data).format("DD/MM/YYYY");

            if (r !== t){
                $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(t);
            }

            e.data("handler").trigger();
        });

        return Bennu.widgetHandler.makeFor(e);
    }

    function timeOptions(e, options){
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
    }

    Bennu.datetime.createTimeWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-time"></span></span><input type="text" data-date-format="HH:mm:ss" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() == "") {
            currentDate = new Date();
        } else {
            currentDate = parseTime(currentDate);
        }
        e.val(moment(currentDate).format("HH:mm:ss.SSS"));

        var options = {
            defaultDate: currentDate,
            language: Bennu.lang,
            pickDate: false,
            pickTime: true,
            useSeconds: true
        }

        timeOptions(e, options);

        $("input", widget).on("change", function (x) {
            x = $(x.target);

            var value = x.val()
            if (value.trim() == ""){
                var r = "";
            }else{
                var r = moment(parseTime(value)).format("HH:mm:ss.SSS");
            }

            if (r !== e.val()){
                e.val(r);
                e.trigger("change");
            }
        }).datetimepicker(options);

        e.after(widget);

        e.on("change.bennu", function(ev){
            var data = $(e).val();

            if (data.trim() == "") {
                data = new Date();
            } else {
                data = parseTime(data);
            }
            e.val(moment(data).format("HH:mm:ss.SSS"));
            var r = $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(moment(data).format("HH:mm:ss"));
            var t = moment(data).format("HH:mm:ss");

            if (r !== t){
                $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(t);
            }

            e.data("handler").trigger();
        });

        return Bennu.widgetHandler.makeFor(e);
    }

    Bennu.datetime.createDateTimeWidget = function (e) {
        e = $(e);
        var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">' +
            '<span class="glyphicon glyphicon-calendar"></span></span><input data-date-format="DD/MM/YYYY HH:mm:ss" type="text" class="bennu-datetime-input form-control"/></div>');

        var currentDate = e.val();

        if (currentDate.trim() == "") {
            currentDate = new Date();
        } else {
            currentDate = new Date(currentDate);
        }

        e.val(moment(currentDate).format("YYYY-MM-DDTHH:mm:ss.SSSZ"));

        var options = {
            sideBySide: true,
            defaultDate: currentDate,
            language: Bennu.lang,
            pickDate: true,
            pickTime: true,
            useSeconds: true,
            showToday: true,
        };

        dateOptions(e,options);
        timeOptions(e,options);

        $("input", widget).on("change", function (x) {
            x = $(x.target);
            var value = x.val().trim()
            if (value == ""){
                e.val("");
            }else{
                var r = moment(value, "DD/MM/YYYY HH:mm:ss").format("YYYY-MM-DDTHH:mm:ss.SSSZ");
                if (r !== e.val()){
                    e.val(r);
                    e.trigger("change");
                }
            }
        }).datetimepicker(options);

        e.after(widget);
        e.data("input");
        widget.data("related", e);

        e.on("change.bennu", function(ev){
            var data = $(e).val();

            if (data.trim() == "") {
                data = new Date();
            } else {
                data = new Date(data);
            }
            e.val(moment(data).format("YYYY-MM-DDTHH:mm:ss.SSSZ"));

            var r = $(".bennu-datetime-input", widget).data("DateTimePicker").getDate().format("DD/MM/YYYY HH:mm:ss");
            var t = moment(data).format("DD/MM/YYYY HH:mm:ss");

            if (r !== t){
                $(".bennu-datetime-input", widget).data("DateTimePicker").setDate(t);
            }

            e.data("handler").trigger();
        });
        return Bennu.widgetHandler.makeFor(e);
    }
}());
