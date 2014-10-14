(function () {
    Bennu.datetime = Bennu.datetime || {}
    Bennu.datetime.attr = "bennu-datetime"
    Bennu.datetime.createWidget = function (e) {
            e = $(e);
            var widget = $('<div class="bennu-datetime-input-group input-group date"><span class="input-group-addon">'+
                '<span class="glyphicon glyphicon-calendar"></span></span><input type="text" class="bennu-datetime-input form-control"/></div>');

            var currentDate = e.val();

            if (currentDate.trim() == ""){
                currentDate = new Date();
            }else{
                currentDate = new Date(currentDate);
            }
            var options = {
                useSeconds: true,
                defaultDate: currentDate,
                sideBySide: true,
                pickDate: true,
                pickTime: true,
                useCurrent:false,
                pickTime: true,
                language: Bennu.lang
            };

            if($("[bennu-datetime]").attr("select-date") === ""){
                options.pickDate = true;
                options.pickTime = false;
            }

            if($("[bennu-datetime]").attr("select-time") === ""){
                options.pickDate = false;
                options.pickTime = true;
            }

            if($("[bennu-datetime]").attr("select-datetime") === ""){
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
            widget.data("related",e)
        }

    $(function () {
        $("[" + Bennu.datetime.attr + "]").each(function(i,e){
            Bennu.datetime.createWidget(e);
        });

        Bennu.monitor.checkFor(Bennu.datetime.attr, {
            add: function(e){
                Bennu.datetime.createWidget(e);
            },
            remove: function(e){
                if ($(e).attr(Bennu.datetime.attr) === ""){
                    $(e).data("input").remove();
                }
            }
        });
    });
}());
