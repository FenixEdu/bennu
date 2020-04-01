(function () {

    function editor(element, handler) {

        Bennu.htmlEditor.components.setTitle("YouTube");
        Bennu.htmlEditor.components.setSubtitle("Add an Embedded YouTube Video");

        let output =
            '<div class="form-group">' +
            '<label class="col-sm-2 control-label">YouTube Video ID</label>' +
            '<div class="col-sm-10">' +
            '<input type="text" class="form-control" id="id" title="Please insert only the video ID" placeholder="XXXXXXXXXX">' +
            '<p class="help-block">Please insert only the video ID</p>' +
            '<p class="help-block">E.g: https://www.youtube.com/watch?v=<b>ABCDEFGHIJK</b></p>' +
            '</div>' +
            '</div>' +
            '<div class="form-group">' +
            '<label class="col-sm-2 control-label">Width</label>' +
            '<div class="col-sm-4">' +
            '<input type="number" class="form-control" title="Specify the width (if needed)" id="width" placeholder="Width" value="560" min="1" max="1920">' +
            '<p></p>' +
            '</div>' +
            '<label class="col-sm-2 control-label">Height</label>' +
            '<div class="col-sm-4">' +
            '<input type="number" class="form-control" title="Specify the height (if needed)" id="height" placeholder="Height" value="315" min="1" max="1080">' +
            '<p></p>' +
            '</div>' +
            '</div>';

        output = $('<div class="form-horizontal">' + output + '</div>');

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton("Add");

        Bennu.htmlEditor.components.clickPrimaryButton(function () {

            handler.restore();

            let id = $("#id", output).val();
            let h = $("#height", output).val();
            let w = $("#width", output).val();

            let height = parseInt(h) || 315;
            let width = parseInt(w) || 560;

            if (id.match("[a-zA-Z0-9_-]{11}")) { // Matches a current Youtube Video ID
                handler.text(`<iframe width="${width}" height="${height}" src="https://www.youtube.com/embed/${id}"></iframe>`);
                Bennu.htmlEditor.components.hideModal();
            } else {
                Bennu.validation.addError($("#id", output).closest(".form-group"));
            }
        });
    }

    function preview(element) {
        element.html('<div class="drop-box"></div>');
        let x = $(".drop-box", element);
        x.css({
            display: "inline-block",
            height: parseInt(element.data("height")),
            width: parseInt(element.data("width"))
        });
        element.attr("contenteditable", "false");
    }

    Bennu.htmlEditor.components.callback.youtube = editor;
    Bennu.htmlEditor.components.preview.youtube = preview;

})();
