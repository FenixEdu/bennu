(function(){

    function handleFiles(files,handler){
      handler.files(files, function(urls){
        var arr = [];
        for (var i = 0; i < urls.length; i++) {
            var o = urls[i];
            arr.push(Bennu.htmlEditor.components.callback.image.renderer(o));
        }

        if (arr.length == 1){
          Bennu.htmlEditor.components.callback.image.renderer(urls[0],function(e){
            editor(e,handler,true);
          });
        }else{
          handler.text(arr);
          Bennu.htmlEditor.components.hideModal();
        }
      });
    }

    function uploader(element, handler){
      Bennu.htmlEditor.components.setTitle("Images");
      Bennu.htmlEditor.components.setSubtitle("Add a picture");

      var uploader = '<div><style>.dragover{'+
        "border: 1px dashed #39f;"+
        "color: #39f;"+
        "transition: all 0.15s ease-in-out;"+
      '}</style>'+
      '<div class="drop-box">'+
        '<p><span>Drag and Drop files</span>Upload files to this directory</p>'+
      '</div>' +

      '<p class="help-block">Or just select a file</p>'+

      '<div class="form-group">'+
        '<div class="col-sm-12">'+
        '<input type="file" class="" id="file" placeholder="file" multiple\\>'+
      '</div>'+
      '</div></div>';

      uploader = $('<div class="form-horizontal">' + uploader + '</div>');

      Bennu.htmlEditor.components.setBody(uploader);
      Bennu.htmlEditor.components.showPrimaryButton();
      Bennu.htmlEditor.components.setPrimaryButton("Make");
      Bennu.htmlEditor.components.hidePrimaryButton();


      // Drag and Drop box
      $(".drop-box", uploader).on("dragenter dragover", function(evt){
        evt.stopPropagation();
        evt.preventDefault();
        $(".drop-box", uploader).addClass("dragover");
      });
      $(".drop-box", uploader).on("dragexit dragleave", function(evt){
        evt.stopPropagation();
        evt.preventDefault();
        $(".drop-box", uploader).removeClass("dragover");
      });

      $(".drop-box", uploader).on("drop", function(evt){
        $(".drop-box", uploader).removeClass("dragover");

        var dataTransfer = evt.originalEvent.dataTransfer;
        evt.stopPropagation();
        evt.preventDefault();
        if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
          handleFiles(dataTransfer.files,handler);
        }
      });

      // Input file box
      $("#file", uploader).on("click",function(){
        this.value = null;
      })

      $("#file", uploader).on("change",function(){
        handleFiles(this.files,handler);
      })
    }

    function editor(element, handler,isnew){
      element = $(element);
      Bennu.htmlEditor.components.setTitle("Images");
      Bennu.htmlEditor.components.setSubtitle((isnew?"Add":"Edit") + " a picture");

      var editor = '<div class="form-group">'+
      '<h4>Preview</h4>'+
      "<div style='text-align:center'>"+
        "<img class='preview' src='" + element.data("source" )+ "' style='max-width:100%'/>'"+
        "</div>"+
      '</div>'+

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
      '</div>';

      
      editor = $('<div class="form-horizontal">' + editor + '</div>');
      var h = $("#height", editor)
      var w = $("#width", editor);

      h.val(element.data("height"));
      w.val(element.data("width"));

      Bennu.htmlEditor.components.setBody(editor);
      Bennu.htmlEditor.components.showPrimaryButton();
      Bennu.htmlEditor.components.setPrimaryButton(isnew?"Add":"Edit");
      Bennu.htmlEditor.components.clickPrimaryButton(function(){
        handler.restore();

        var height = parseInt(h.val()) || null;
        var width = parseInt(w.val()) || null;


        element.data("height",height);
        element.attr("data-height", height);

        element.data("width",width);
        element.attr("data-width", width);

        handler.text(element);
        Bennu.htmlEditor.components.hideModal();
      });
    }

    Bennu.htmlEditor.components.callback.image = function(element,handler){
      if (element){
        editor(element, handler);
      }else{
        uploader(element, handler);
      }
    };

    Bennu.htmlEditor.components.callback.image.renderer = function(url,cb){
      var img = new Image();

      img.onload = function() {
        var e = $(img);
        e.attr("data-height", this.height);
        e.attr("data-width", this.width);
        cb && cb(img);
      };

      img.setAttribute("bennu-component", "image");
      img.setAttribute("component-resizable", "");
      img.setAttribute("data-source", url);
      img.src = url;

      return $(img);
    }

    function preview(element){
      element.css({
          height:parseInt(element.data("height")),
          width:parseInt(element.data("width"))
      });
    }

    Bennu.htmlEditor.components.preview.image = preview

})();
