(function(){

    function editor(element, handler){
        Bennu.htmlEditor.components.setTitle("YouTube");
        Bennu.htmlEditor.components.setSubtitle((element?"Edit":"Add") + " a Embebed YouTube Video");

        var output = '<div class="form-group">'+
    		'<label for="inputEmail3" class="col-sm-2 control-label">YouTube URL</label>'+
    			'<div class="col-sm-10">'+
    			'<input type="email" class="form-control" id="url" placeholder="Link" \\>'+
    			'<p class="help-block">Use the full URL, including HTTP(S)</p>'+
    		'</div>'+
        '</div>' +

        '<div class="form-group">'+
            '<label for="inputEmail3" class="col-sm-2 control-label">Width</label>'+
            '<div class="col-sm-4">'+
                '<input type="email" class="form-control" id="width" placeholder="Width" \\>'+
                '<p class="help-block"></p>'+
            '</div>'+
            '<label for="inputEmail3" class="col-sm-2 control-label">Height</label>'+
                '<div class="col-sm-4">'+
                '<input type="email" class="form-control" id="height" placeholder="Height" \\>'+
                '<p class="help-block"></p>'+
            '</div>'+
        '</div>'

        ;

        output = $('<div class="form-horizontal">' + output + '</div>');
        var v = $("#url", output);
        var h = $("#height", output)
        var w = $("#width", output);

        if (element){
            v.val(element.data("source"));
            h.val(element.data("height"));
            w.val(element.data("width"));
        }else{
            w.val("560");
            h.val("315");
        }

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.showPrimaryButton();
        Bennu.htmlEditor.components.setPrimaryButton(element?"Edit":"Add");
        Bennu.htmlEditor.components.clickPrimaryButton(function(){
            handler.restore();
            var url = $("#url",output).val();

            var height = parseInt(h.val()) || 315;
            var weight = parseInt(w.val()) || 560;

            if (Bennu.validation.isUrl(url)){
    	        handler.text('<div bennu-component="youtube" component-resizable data-source="' + url + '" data-height="' + height + '" data-width="' + weight + '"></div>');

    	        Bennu.htmlEditor.components.hideModal();
            }else{
            	Bennu.validation.addError($("#url",output).closest(".form-group"));
            }
        });
    }

    function preview(element){
        element.html('<div class="drop-box"></div>');
        var x = $(".drop-box",element);
        x.css({
            display:"inline-block",
            height:parseInt(element.data("height")),
            width:parseInt(element.data("width"))
        });
        element.attr("contenteditable","false");
    }

    Bennu.htmlEditor.components.callback.youtube = editor;
    Bennu.htmlEditor.components.preview.youtube = preview;

})();
