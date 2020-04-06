(function () {
    function editor(element, handler) {

        Bennu.htmlEditor.components.setTitle("Generic Source");
        Bennu.htmlEditor.components.setSubtitle("Embed a video using HTML");

        let output = '<div class="form-group"><label class="col-sm-2 control-label">Embedding Code</label><p></p>' +
            '<div class="col-sm-10"><textarea title="Please copy and paste the code" class="form-control" type="text" id="code" rows="10" cols="80"></textarea>' +
            '<p class="help-block">In most websites the code can be found by clicking "<b>Share/Embed</b>" (or an equivalent option).</p>' +
            '<p class="help-block">Here is an example from YouTube:</p><pre><code>&lt;iframe width="560" height="315" src="https://www.youtube.com/embed/Nmi4jKLTX9w" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen>&lt;/iframe&gt;&nbsp;</code></pre>' +
            '<p></p><p><b>Note</b>: embedding social media posts (like Twitter, Facebook or Instagram) <b>is not supported</b>.</p>' +
            '</div></div>';

        output = $('<div class="form-horizontal">' + output + '</div>');

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton("Add");

        Bennu.htmlEditor.components.clickPrimaryButton(function () {

            handler.restore();

            let code = $("#code", output).val();

            if (code.length > 0) {
                handler.text(code);
                Bennu.htmlEditor.components.hideModal();
            } else {
                Bennu.validation.addError($("#code", output).closest(".form-group"));
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

    Bennu.htmlEditor.components.callback.generic = editor;
    Bennu.htmlEditor.components.preview.generic = preview;
})();