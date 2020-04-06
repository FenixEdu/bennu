(function () {

    function editor(element, handler) {

        Bennu.htmlEditor.components.setTitle("Vimeo");
        Bennu.htmlEditor.components.setSubtitle("Add an Embedded Vimeo Video");


        let output = '<p class="help-block">Please choose <b>one</b> of these options:</p><ul>' +
            '<li><p class="help-block">the video full link</p><p class="help-block">e.g. <b>https://vimeo.com/146820533</b></p>' +
            '</li><li> <p class="help-block">the video ID </p> <p class="help-block">e.g. <b>146820533</b></p></li></ul>' +
            '<div class="form-group"><label class="col-sm-2 control-label">Link or ID</label><p></p>' +
            '<div class="col-sm-8"><textarea type="text" class="form-control" id="link" placeholder="https://vimeo.com/146820533" cols="50" rows="1"></textarea>' +
            '</div></div><p></p><div class="form-group">' +
            '<label class="col-sm-2 control-label">Width</label>' +
            '<div class="col-sm-4"><input type="number" class="form-control" title="Specify the width (if needed)" id="width" placeholder="Width" value="560" min="1" max="1920">' +
            '</div></div><p></p><div class="form-group"><label class="col-sm-2 control-label">Height</label>' +
            '<div class="col-sm-4"><input type="number" class="form-control" title="Specify the height (if needed)" id="height" placeholder="Height" value="315" min="1" max="1080"></div></div>';

        output = $('<div class="form-horizontal">' + output + '</div>');

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton("Add");

        Bennu.htmlEditor.components.clickPrimaryButton(function () {

            handler.restore();

            let link = $("#link", output).val();
            let h = $("#height", output).val();
            let w = $("#width", output).val();

            if(link == null || link.trim().length == 0){
                Bennu.validation.addError($("#link", output).closest(".form-group"));
                return;
            }

            let height = parseInt(h) || 315;
            let width = parseInt(w) || 560;

            function GetVimeoIDbyUrl(url) {
                let id = false;
                $.ajax({
                    url: 'https://vimeo.com/api/oembed.json?url=' + url,
                    async: false,
                    success: function (response) {
                        if (response.video_id) {
                            id = response.video_id;
                        }
                    }
                });
                return id.toString();
            }

            let allNumbers_regex = "^[0-9]+$";
            let vimeoID_regex = "[0-9]{1,10}";

            let video_id = "";

            if( link.match(allNumbers_regex) && link.match(vimeoID_regex) ){
                video_id = link;
            } else {
                video_id = GetVimeoIDbyUrl(link);
            }

            //console.log(video_id);

            if (video_id.match(vimeoID_regex)) { // Sanity check
                handler.text(`<iframe width="${width}" height="${height}" src="https://player.vimeo.com/video/${video_id}"></iframe>`);
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

    Bennu.htmlEditor.components.callback.vimeo = editor;
    Bennu.htmlEditor.components.preview.vimeo = preview;
})();