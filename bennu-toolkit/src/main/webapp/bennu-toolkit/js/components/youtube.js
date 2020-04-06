(function () {

    function editor(element, handler) {

        Bennu.htmlEditor.components.setTitle("YouTube");
        Bennu.htmlEditor.components.setSubtitle("Add an Embedded YouTube Video");

        let output = '<p class="help-block">Please choose <b>one</b> of these options:</p> <ul> <li><p class="help-block">the video full link</p><p class="help-block">e.g. <b>https://youtu.be/Nmi4jKLTX9w</b></p></li> <li> <p class="help-block">the video ID </p> <p class="help-block">e.g. <b>Nmi4jKLTX9w</b></p> </li> </ul><div class="form-group"><label class="col-sm-2 control-label">Link or ID</label><p></p><div class="col-sm-10"><input type="text" class="form-control" id="link" placeholder="https://youtu.be/Nmi4jKLTX9w"></div></div><p></p><div class="form-group"><label class="col-sm-2 control-label">Width</label><div class="col-sm-4"><input type="number" class="form-control" title="Specify the width (if needed)" id="width" placeholder="Width" value="560" min="1" max="1920"></div></div><p></p><div class="form-group"><label class="col-sm-2 control-label">Height</label><div class="col-sm-4"><input type="number" class="form-control" title="Specify the height (if needed)" id="height" placeholder="Height" value="315" min="1" max="1080"></div></div>';

        output = $('<div class="form-horizontal">' + output + '</div>');

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton("Add");

        Bennu.htmlEditor.components.clickPrimaryButton(function () {

            handler.restore();

            let link = $("#link", output).val();

            if(link == null || link.trim().length == 0){
                Bennu.validation.addError($("#link", output).closest(".form-group"));
                return;
            }

            let h = $("#height", output).val();
            let w = $("#width", output).val();

            let height = parseInt(h) || 315;
            let width = parseInt(w) || 560;


            let allAllowedChars_regex = "^[0-9A-z-_]+$";
            let youtubeID_regex = "[0-9A-z-_]{11}";
            let url_regex = "(\\/|%3D|v=)([0-9A-z-_]{11})([%#?&]|$)";

            let video_id = "";

            if(link.match(allAllowedChars_regex) && link.match(youtubeID_regex) ){
                video_id = link;
            } else {
                let matches = link.match(url_regex);
                if(matches.length >= 3){
                    video_id = matches[2];
                }
            }

            if (video_id.match(youtubeID_regex)) { // Sanity check
                handler.text(`<iframe width="${width}" height="${height}" src="https://www.youtube.com/embed/${video_id}"></iframe>`);
                Bennu.htmlEditor.components.hideModal();
            } else {
                Bennu.validation.addError($("#link", output).closest(".form-group"));
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
