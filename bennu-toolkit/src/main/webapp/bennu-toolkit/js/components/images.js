(function () {

    function editor(element, handler) {

        element = $(element);

        Bennu.htmlEditor.components.setTitle("Images");
        Bennu.htmlEditor.components.setSubtitle("Add a picture");

        let editor = '<div class="form-group">' +
            '<label class="col-sm-2 control-label">Link</label>' +
            '<div class="col-sm-10">' +
            '<input type="url" class="form-control" title="Insert the picture link here" id="link" placeholder="https://">' +
            '<p class="help-block">Use the full URL to the picture, including HTTP(S)</p>' +
            '</div></div>' +
            '<p></p>' +
            '<div class="form-group">' +
            '<label class="col-sm-2 control-label">Scale</label>' +
            '<div class="col-sm-2">' +
            '<input type="number" class="form-control" id="scale" placeholder="Scale" value="50" min="0" max="100">' +
            '<p></p>' +
            '</div></div>' +
            '<div class="form-group">' +
            '<label class="col-sm-2 control-label">Alt-text</label>' +
            '<div class="col-sm-4">' +
            '<input type="text" class="form-control" id="alt" placeholder="Image Description" value="">' +
            '<p></p>' +
            '</div></div>';

        editor = $('<div class="form-horizontal">' + editor + '</div>');

        function getMeta(url) {
            let img = new Image();
            let w = -1, h = -1;
            img.addEventListener("load", function () {
                w = this.naturalWidth;
                h = this.naturalHeight;
            });
            img.src = url;
            return [w, h];
        }

        let s = $("#scale", editor);
        let u = $("#link", editor);
        let a = $("#alt", editor);

        Bennu.htmlEditor.components.setBody(editor);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton("Add");
        Bennu.htmlEditor.components.clickPrimaryButton(function () {

            handler.restore();

            let scale = parseInt(s.val()) || 50;
            let url = u.val() || null;
            let values = getMeta(url);
            console.log(values)
            let alt = a.val() || "";

            if (url) {
                if (Bennu.validation.isUrl(url)) {
                    if ($(element).parent().is("a")) {
                        $(element).unwrap();
                    }
                    handler.text(`<img style="max-width:${scale}vw" alt="${alt}" src="${url}"/>`);

                    Bennu.htmlEditor.components.hideModal();
                } else {
                    Bennu.validation.addError($("#link", editor).closest(".form-group"));
                }
            }
        });
    }

    Bennu.htmlEditor.components.callback.image = function (element, handler) {
        editor(element, handler);
    };

    Bennu.htmlEditor.components.callback.image.renderer = function (url, cb) {
        let img = new Image();

        img.onload = function () {
            let e = $(img);
            e.attr("data-height", this.height);
            e.attr("data-width", this.width);
            cb && cb(img);
        };

        img.setAttribute("bennu-component", "image");
        img.setAttribute("component-resizable", "");
        img.setAttribute("data-source", url);
        img.src = url;

        return $(img);
    };

    function preview(element) {
        element.css({
            height: parseInt(element.data("height")),
            width: parseInt(element.data("width"))
        });
    }

    Bennu.htmlEditor.components.preview.image = preview

})();
