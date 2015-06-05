function editor(element, handler){
    Bennu.htmlEditor.components.setTitle("Link");
    Bennu.htmlEditor.components.setSubtitle((element?"Edit":"Add") + " a hyperlink");

    var output = '<div class="form-group">'+
		'<label for="inputEmail3" class="col-sm-2 control-label">Text</label>'+
			'<div class="col-sm-10">'+
			'<input type="email" class="form-control" id="text" placeholder="Text" value="" \\>'+
			'<p class="help-block"></p>'+
		'</div>'+
    '</div>' +

    '<div class="form-group">'+
		'<label for="inputEmail3" class="col-sm-2 control-label">Link</label>'+
			'<div class="col-sm-10">'+
			'<input type="email" class="form-control" id="url" placeholder="Link" \\>'+
			'<p class="help-block">Use the full URL, including HTTP(S)</p>'+
		'</div>'+
    '</div>';

    output = $('<div class="form-horizontal">' + output + '</div>');
    var z = $("#text", output);
    var v = $("#url", output);
    if (element){
        z.val(element.html());
        v.val(element.attr("href"));
    }else if(handler.selection){
        z.val(handler.text());
    }

    Bennu.htmlEditor.components.setBody(output);
    Bennu.htmlEditor.components.showPrimaryButton();
    Bennu.htmlEditor.components.setPrimaryButton(element?"Edit":"Add");
    Bennu.htmlEditor.components.clickPrimaryButton(function(){
        handler.restore();
        var url = $("#url",output).val();

        if (Bennu.validation.isUrl(url)){
        	var content = z.val();

        	content = content || url;

	        handler.text('<a bennu-component="link" href="' + url + '" target="_blank">' + content + '</a>');

	        Bennu.htmlEditor.components.hideModal();
        }else{
        	Bennu.validation.addError($("#url",output).closest(".form-group"));
        }
    });
}

Bennu.htmlEditor.components.callback.link = editor;
