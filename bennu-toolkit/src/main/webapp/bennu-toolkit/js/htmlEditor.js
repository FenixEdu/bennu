(function () {
    /** @const */ var ALLOW_VOICE = false;
    /** @const */ var ALLOW_FULLSCREEN = true;

    
    Bennu.htmlEditor = Bennu.htmlEditor || {};
    Bennu.htmlEditor.attr = "bennu-html-editor";
    Bennu.htmlEditor.colors = [
        ["#000000","#424242","#636363","#9C9C94","#CEC6CE","#EFEFEF","#F7F7F7","#FFFFFF"],
        ["#FF0000","#FF9C00","#FFFF00","#00FF00","#00FFFF","#0000FF","#9C00FF","#FF00FF"],
        ["#F7C6CE","#FFE7CE","#FFEFC6","#D6EFD6","#CEDEE7","#CEE7F7","#D6D6E7","#E7D6DE"],
        ["#E79C9C","#FFC69C","#FFE79C","#B5D6A5","#A5C6CE","#9CC6EF","#B5A5D6","#D6A5BD"],
        ["#E76363","#F7AD6B","#FFD663","#94BD7B","#73A5AD","#6BADDE","#8C7BC6","#C67BA5"],
        ["#CE0000","#E79439","#EFC631","#6BA54A","#4A7B8C","#3984C6","#634AA5","#A54A7B"],
        ["#9C0000","#B56308","#BD9400","#397B21","#104A5A","#085294","#311873","#731842"],
        ["#630000","#7B3900","#846300","#295218","#083139","#003163","#21104A","#4A1031"]];

    Bennu.htmlEditor.saveSelection = function() {
        if (window.getSelection) {
            sel = window.getSelection();
            if (sel.getRangeAt && sel.rangeCount) {
                return sel.getRangeAt(0);
            }
        } else if (document.selection && document.selection.createRange) {
            return document.selection.createRange();
        }
        return null;
    }

    Bennu.htmlEditor.restoreSelection = function(range) {
        if (range) {
            if (window.getSelection) {
                sel = window.getSelection();
                sel.removeAllRanges();
                sel.addRange(range);
            } else if (document.selection && range.select) {
                range.select();
            }
        }
    }

    function generateColors(type){
        var result="";
        for (var i = 0; i < Bennu.htmlEditor.colors.length; i++) {
            var l = Bennu.htmlEditor.colors[i]
            var line = '<div class="note-color-row">'
            for (var j = 0; j < l.length; j++) {
                var color = l[j]
                line += '<button type="button" class="note-color-btn" style="background-color:' + color + ';" data-event="' + type + '" data-value="' + color + '" title="" data-toggle="button" tabindex="-1" data-original-title="' + color + '"></button>'
            };
            line += '</div>'
            result += line;
        };
        return result;
    }

    Bennu.htmlEditor.fullscreen = function (e) {
        var target = $(e.target);
        var a = target.closest(".bennu-html-editor-input");
        a.toggleFullScreen();

        a.addClass("fullscreen");
        $(".fullscreen-button", a).addClass("btn-primary");
        $(document).bind("fullscreenchange", function () {
            var state = ($(document).fullScreen() ? "on" : "off");
            if (state == "off") {
                $(".fullscreen").removeClass("fullscreen");
                $(".fullscreen-button", a).removeClass("btn-primary");
                $(".bennu-html-editor-tools", a).off("mouseover mouseout");
                $(".switch-to-code",a).parent().show()
                a.removeClass("visible");
            }else{
                $(".switch-to-code",a).parent().hide()
            }
        });
        var i;

        function h(action) {
            clearTimeout(i),
                    a.hasClass("visible") && "show" !== action ? "autohide" !== action &&
                a.removeClass("visible") : a.addClass("visible"),
                "autohide" === action && (i = setTimeout(function () {
                a.removeClass('visible')
            }, 2e3))
        }

        $(".bennu-html-editor-tools", a).on("mouseover", function () {
            h("show");
        }).on("mouseout", function () {
            h("autohide");
        })
        h("show");
        i = setTimeout(function () {
            a.removeClass('visible')
        }, 2e3);

        $(".bennu-html-editor-editor", a).focus();
    }

    var two_line = /\n\n/g;
    var one_line = /\n/g;

    function linebreak(s) {
        return s.replace(two_line, '<p></p>').replace(one_line, '<br>');
    }

    var first_char = /\S/;

    function capitalize(s) {
        return s.replace(first_char, function (m) {
            return m.toUpperCase();
        });
    }


    Bennu.htmlEditor.createWidget = function (e) {
        e = $(e)
        var dom = $('<div class="bennu-html-editor-input">' +
            '<div class="bennu-html-editor-tools">' +
            '<div class="bennu-html-editor-toolbar btn-toolbar" data-role="editor-toolbar" data-target=".bennu-html-editor-editor"></div>' +
            '</div>' +
            '<div class="bennu-html-editor-editor form-control editor-input" contenteditable="true"></div>' +
            '<span class="help-block"></span>' +
            '</div>');

        var toolbarReqs = e.attr("toolbar");
        if (toolbarReqs === "" || toolbarReqs === undefined || toolbarReqs === null) {
            toolbarReqs = "size,style,lists,align,links,table,image,undo,fullscreen,source";
        }
        $()
        toolbarReqs = toolbarReqs.split(",");

        for (var i = 0; i < toolbarReqs.length; i++) {
            var c = toolbarReqs[i];
            if (c === "size") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Style">' +
                    '<span class="fa fa-header"></span>&nbsp;<b class="caret"></b></a>' +
                    '<ul class="dropdown-menu">'+
                    '<li><a data-edit="formatBlock p" href="#" data-value="">Normal</a></li>'+
                    '<li><a data-edit="formatBlock blockquote" href="#" data-value="">Quote</a></li>'+
                    '<li><a data-edit="formatBlock pre" href="#">Code</a></li>'+
                    '<li><a data-edit="formatBlock h1" href="#">Header 1</a></li>'+
                    '<li><a data-edit="formatBlock h2" href="#">Header 2</a></li>'+
                    '<li><a data-edit="formatBlock h3" href="#">Header 3</a></li>'+
                    '<li><a data-edit="formatBlock h4" href="#">Header 4</a></li>'+
                    '<li><a data-edit="formatBlock h5" href="#">Header 5</a></li>'+
                    '<li><a data-edit="formatBlock h6" href="#">Header 6</a></li>'+
                    '</ul>'+
                    '</div>');
            } else if (c === "style") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="bold" title="" data-original-title="Bold (Ctrl/Cmd+B)"><span class="glyphicon glyphicon-bold"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="italic" title="" data-original-title="Italic (Ctrl/Cmd+I)"><span class="glyphicon glyphicon-italic"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="strikethrough" title="" data-original-title="Strikethrough"><span class="fa fa-strikethrough"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="underline" title="" data-original-title="Underline (Ctrl/Cmd+U)"><span class="fa fa-underline"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="superscript" title="" data-original-title="Superscript"><i class="fa fa-superscript icon-superscript"></i></a>'+
                    '<a class="btn btn-sm btn-default" data-edit="subscript" title="" data-original-title="Subscript"><i class="fa fa-subscript icon-subscript"></i></a>'+
                    '<a class="btn btn-sm btn-default" data-edit="removeFormat" title="" data-original-title="Remove Font Style"><i class="fa fa-eraser"></i></a>'+
                    '</div>');
            } else if (c === "lists") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="insertunorderedlist" title="" data-original-title="Bullet list"><span class="fa fa-list-ul"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="insertorderedlist" title="" data-original-title="Number list"><span class="fa fa-list-ol"></span></a>' +
                    '</div>');
            } else if (c === "align") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Paragraph">' +
                    '<span class="fa fa-align-left"></span>&nbsp;<b class="caret"></b></a>'+
                    '<ul class="dropdown-menu menu-paragraph"><li>'+
                    '<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="outdent" title="" data-original-title="Reduce indent (Shift+Tab)"><span class="glyphicon glyphicon-indent-left"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="indent" title="" data-original-title="Indent (Tab)"><span class="glyphicon glyphicon-indent-right"></span></a>' +
                    '</div>'+
                    '<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default btn-primary" data-edit="justifyleft" title="" data-original-title="Align Left (Ctrl/Cmd+L)"><span class="glyphicon glyphicon-align-left"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifycenter" title="" data-original-title="Center (Ctrl/Cmd+E)"><span class="glyphicon glyphicon-align-center"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifyright" title="" data-original-title="Align Right (Ctrl/Cmd+R)"><span class="glyphicon glyphicon-align-right"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifyfull" title="" data-original-title="Justify (Ctrl/Cmd+J)"><span class="glyphicon glyphicon-align-justify"></span></a>' +
                    '</div></li></ul></div>');
            } else if (c === "links") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default link-to-add-ui-btn dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Hyperlink"><span class="glyphicon glyphicon-link"></span></a>' +
                    '<div class="dropdown-menu input-append add-url">' +

                    '<div class="input-group"><input type="text" placeholder="URL" class="link-to-add form-control"><div class="input-group-btn"><button type="button" class="btn btn-default link-to-add-btn" aria-expanded="false">Add</button></div></div>'+

                    '</div>' +
                    '<a class="btn btn-sm btn-default" data-edit="unlink" title="" data-original-title="Remove Hyperlink"><span class="fa fa-chain-broken"></span></a>' +
                    '</div>');
            } else if (c === "table"){
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<button type="button" class="btn btn-default btn-sm btn-small dropdown-toggle" data-toggle="dropdown" title="" tabindex="-1" data-original-title="Table"><i class="fa fa-table icon-table"></i> <span class="caret"></span></button>'+

                    '<div class="dropdown-menu input-append note-table">'+
                        '<div class="note-dimension-picker">' +
                           '<div class="note-dimension-picker-mousecatcher" data-event="insertTable" data-value="1x1"></div>' +
                           '<div class="note-dimension-picker-highlighted"></div>' +
                           '<div class="note-dimension-picker-unhighlighted"></div>' +
                         '</div>' +
                         '<div class="note-dimension-display"> 1 x 1 </div>' +
                    '</div>'+
                    '</div>');
            } else if (c === "colors"){
                $(".btn-toolbar", dom).append('<div class="note-color btn-group">'+
                    '<button type="button" class="btn btn-default btn-sm btn-small note-recent-color" title="" data-event="color" data-value="{&quot;backColor&quot;:&quot;yellow&quot;}" tabindex="-1" data-original-title="Recent Color">'+
                        '<i class="fa fa-font icon-font" style="color:black;background-color:yellow;"></i>'+
                    '</button>'+
                    '<button type="button" class="btn btn-default btn-sm btn-small dropdown-toggle" data-toggle="dropdown" title="" tabindex="-1" data-original-title="More Color">'+
                        '<span class="caret"></span>'+
                    '</button>'+
                    '<ul class="dropdown-menu">'+
                    '<li>'+
                        '<div class="btn-group">'+
                            '<div class="note-palette-title">Background Color</div>'+
                            '<a class="note-color-reset" href="#" data-event="backColor" data-value="inherit" title="Transparent">Set transparent</a>'+

                            '<div class="note-color-palette" data-target-event="backColor">'+
                                generateColors("backColor")+
                            '</div>'+
                        '</div>'+
                        
                        
                        '<div class="btn-group">'+
                            '<div class="note-palette-title">Foreground Color</div>'+
                            '<a href="#" class="note-color-reset" data-event="foreColor" data-value="inherit" title="Reset">Reset to default</a>'+

                            '<div class="note-color-palette" data-target-event="foreColor">'+
                                generateColors("foreColor")+
                            '</div>'+
                        '</div>'+
                    '</li>'+

                    '</ul></div>');
            } else if (c === "image") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                        '<a class="pictureBtn btn btn-sm btn-default" title="" data-original-title="Insert picture (or just drag &amp; drop)"><span class="glyphicon glyphicon-picture"></span></a>' +
                        '<input type="file" name="pictureTlb" style="position:absolute; top: -1000px;" multiple>'+
                        '</div>');
            } else if (c === "undo") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="undo" title="" data-original-title="Undo (Ctrl/Cmd+Z)"><span class="fa fa-undo"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="redo" title="" data-original-title="Redo (Ctrl/Cmd+Y)"><span class="fa fa-repeat"></span></a>' +
                    '</div>');
            } else if (c === "fullscreen" && ALLOW_FULLSCREEN) {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a href="#" title="" data-original-title="Enter Zen Mode" class="btn btn-sm btn-default fullscreen-button"><span class="fa fa-arrows-alt"></span></a>'+
                    '</div>');
                $(".fullscreen-button", dom).on("click", Bennu.htmlEditor.fullscreen);
            } else if (c === "source"){
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default switch-to-code" title="" data-original-title="Switch to Source"><span class="fa fa-code"></span></a>' +
                    '</div>');

                $(".bennu-html-editor-editor", dom).after("<div class='bennu-html-code-editor-container' style='display:none;'>"+
                    "<pre class='bennu-html-code-editor'></pre>" +
                    "</div>");
            }
        }


        $('a[title]', dom).tooltip({container: 'body'});

        $('[data-role=magic-overlay]', dom).each(function () {
            var overlay = $(this), target = $(overlay.data('target'));
            overlay.css('opacity', 0).css('position', 'absolute').offset(target.offset()).width(target.outerWidth()).height(target.outerHeight());
        });

        $('.dropdown-menu input', dom).click(function () {
            return false;
        })
            .change(function () {
                $(this).parent('.dropdown-menu').siblings('.dropdown-toggle').dropdown('toggle');
            })
            .keydown('esc', function () {
                this.value = '';
                $(this).change();
            });

        dom.data("related", e);
        e.data("input", dom)

        function showErrorAlert(reason, detail) {
            var msg = '';
            if (reason === 'unsupported-file-type') {
                msg = "Unsupported format " + detail;
            }
            else {
                console.log("error uploading file", reason, detail);
            }
            $('<div class="alert"> <button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '<strong>File upload error</strong> ' + msg + ' </div>').prependTo('#alerts');
        }

        var s = new Sanitize(Sanitize.Config.RELAXED);

        $('.bennu-html-editor-editor', dom).on("paste", function () {
            setTimeout(function () {
                $('.bennu-html-editor-editor', dom).html(s.clean_node($('.bennu-html-editor-editor', dom)[0]));
            });
        });

        $('.bennu-html-editor-editor', dom).on('focus', function () {
            var $this = $(this);
            $this.data('before', $this.html());

            return $this;
        }).on('blur keyup paste input', function () {
            var $this = $(this);

            if ($this.data('before') !== $this.html()) {
                $this.data('before', $this.html());
                $this.trigger('change');
            }

            return $this;
        });

        var widgetInput = {};
        widgetInput.val = function () {
            if (arguments.length === 0) {
                return $(".bennu-html-editor-editor", dom).html();
            } else {
                return $(".bennu-html-editor-editor", dom).html(arguments[0]);
            }
        }

        var attr = e.attr("bennu-localized-string");
        if (attr != null && attr != undefined) {
            var menu = $('<div class="btn-group bennu-localized-string-group">' +
                '<button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown">' +
                '<span class="bennu-localized-string-language"></span> <span class="caret"></span>' +
                '</button>' +
                '<ul class="dropdown-menu bennu-localized-string-menu" role="menu"></ul></div>');
            $("div.btn-toolbar", dom).before(menu);
            dom.data("localized-string", true);

            Bennu.localizedString.makeLocaleList($(".bennu-localized-string-menu", dom), dom, function (e) {
                Bennu.localizedString.changeData($(e.target).parent().data("locale"), $(".bennu-localized-string-language", dom), widgetInput, dom);
            });

            Bennu.localizedString.changeData(Bennu.locale, $(".bennu-localized-string-language", dom), widgetInput, dom);

            if (e.val() === "") {
                e.val("{}");
            }
        }else{
            var val = e.val();
            if (val){
                $(".bennu-html-editor-editor", dom).html(e.val());    
            }else{
                $(".bennu-html-editor-editor", dom).html("");    
            }
            
        }

        $(".bennu-html-editor-editor", dom).on('change', function () {
            var attr = e.attr("bennu-localized-string");
            if (attr !== null && attr !== undefined) {
                var data = JSON.parse($(dom.data("related")).val());
                var locale = $(".bennu-localized-string-language", dom).data("locale");
                var tag = locale.tag;

                if (!(tag in data)){
                    var singleTag = locale.tag.split("-")[0].toLowerCase();
                    if (singleTag in data){
                        tag = singleTag;
                    }
                }

                data[tag] = widgetInput.val();
                $(dom.data("related")).val(JSON.stringify(data));
                $(".help-block", dom).empty();
                dom.removeClass("has-error");
                dom.data("related").trigger("change");
            } else {
                var r = dom.data("related").val();
                var t = $(".bennu-html-editor-editor", dom).html();
                if (r !== t) {
                    dom.data("related").val($(".bennu-html-editor-editor", dom).html());
                    dom.data("related").trigger("change");
                };
            }
        });

        $(".btn-toolbar .pictureBtn", dom).on("click", function () {
            $(".btn-toolbar input[name='pictureTlb']", dom).click();
        });

        var attachPicture = function(urls){

                $(".bennu-html-editor-editor", dom).focus()

                for (var i = 0; i < urls.length; i++) {
                    var o = urls[i];
                    document.execCommand('insertimage', 0, o);
                }
            };

        $(".btn-toolbar input[name='pictureTlb']", dom).on("change", function (evt) {
            var z = e.data("fileHandler");
            z && z($(".btn-toolbar input[name='pictureTlb']", dom)[0].files, attachPicture);
        });

        dom.on('dragenter dragover', false)
            .on('drop', function (evt) {
                var dataTransfer = evt.originalEvent.dataTransfer;
                evt.stopPropagation();
                evt.preventDefault();
                if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
                    var z = e.data("fileHandler")
                    z && z(dataTransfer.files, attachPicture);
                }
            });
        $('.bennu-html-editor-editor', dom).wysiwyg({ dragAndDropImages: false, fileUploadError: showErrorAlert});

        Bennu.validation.attachToForm(dom);
        e.after(dom);

        // table shit

        var PX_PER_EM = 18;
    var hDimensionPickerMove = function (event, options) {
      var $picker = $(event.target.parentNode); // target is mousecatcher
      var $dimensionDisplay = $picker.next();
      var $catcher = $picker.find('.note-dimension-picker-mousecatcher');
      var $highlighted = $picker.find('.note-dimension-picker-highlighted');
      var $unhighlighted = $picker.find('.note-dimension-picker-unhighlighted');

      var posOffset;
      // HTML5 with jQuery - e.offsetX is undefined in Firefox
      if (event.offsetX === undefined) {
        var posCatcher = $(event.target).offset();
        posOffset = {
          x: event.pageX - posCatcher.left,
          y: event.pageY - posCatcher.top
        };
      } else {
        posOffset = {
          x: event.offsetX,
          y: event.offsetY
        };
      }

      var dim = {
        c: Math.ceil(posOffset.x / PX_PER_EM) || 1,
        r: Math.ceil(posOffset.y / PX_PER_EM) || 1
      };

      $highlighted.css({ width: dim.c + 'em', height: dim.r + 'em' });
      $catcher.attr('data-value', dim.c + 'x' + dim.r);

      if (3 < dim.c && dim.c < 10) {
        $unhighlighted.css({ width: dim.c + 1 + 'em'});
      }

      if (3 < dim.r && dim.r < 10) {
        $unhighlighted.css({ height: dim.r + 1 + 'em'});
      }

      $dimensionDisplay.html(dim.c + ' x ' + dim.r);
    };

    var createTable = function (colCount, rowCount) {
      var tds = [], tdHTML;
      for (var idxCol = 0; idxCol < colCount; idxCol++) {
        tds.push('<td>' + "&nbsp;" + '</td>');
      }
      tdHTML = tds.join('');

      var trs = [], trHTML;
      for (var idxRow = 0; idxRow < rowCount; idxRow++) {
        trs.push('<tr>' + tdHTML + '</tr>');
      }
      trHTML = trs.join('');
      return '<table class="table table-bordered">' + trHTML + '</table>';
    };

    var $catcher = $(".btn-toolbar", dom).find('.note-dimension-picker-mousecatcher');
      $catcher.css({
        width: 10 + 'em',
        height: 10 + 'em'
      }).on('mousemove', function (event) {
        hDimensionPickerMove(event, {});
      });

      $(".note-dimension-picker-mousecatcher", dom).on("click",function(){
        var val = $(this).attr("data-value").split("x");
        $(".bennu-html-editor-editor", dom).focus();
        document.execCommand("insertHTML", false,createTable(parseInt(val[0]), parseInt(val[1])));
      });

        // end table shit


        $(".link-to-add-ui-btn",dom).on("click",function(){
            var select = Bennu.htmlEditor.saveSelection();
            $(".bennu-html-editor-editor", dom).data("save-selection",select);
            setTimeout(function(){
                $(".link-to-add",dom).focus();
            },100);
        });


        $(".link-to-add-btn", dom).on("click",function(){
            var val = $(".bennu-html-editor-editor", dom).data("save-selection");
            $(".bennu-html-editor-editor",dom).focus();
            setTimeout(function(){
                if (val){
                    Bennu.htmlEditor.restoreSelection(val);
                }

                document.execCommand("CreateLink", false, $(".link-to-add").val());
                $(".link-to-add").val("")
            });
        });

        e.on("change.bennu", function (ev) {
            var attr = e.attr("bennu-localized-string");
            if (attr !== null && attr !== undefined) {
	        var value = $(e).val();
		if (value === null || value === undefined || value === "") {
		   value = "{}";
		   $(e).val(value);
		}
                var data = JSON.parse(value);
                var locale = $(".bennu-localized-string-language", dom).data("locale");
                var tag = locale.tag

                if (!(tag in data)){
                    var singleTag = locale.tag.split("-")[0].toLowerCase();
                    if (singleTag in data){
                        tag = singleTag;
                    }
                }

                var t = data[tag] || "";
                var r = $(".bennu-html-editor-editor", dom).html();

                if (r !== t){
                    $(".bennu-html-editor-editor", dom)[0].innerHTML = t;
                }
            } else {
                var t = $(e).val()
                var r = $(".bennu-html-editor-editor", dom).html();
                if (r !== t){
                    $(".bennu-html-editor-editor", dom)[0].innerHTML = t;
                }
            }
	    e.data("handler").trigger();
        });

        var handler = Bennu.widgetHandler.makeFor(e);

        var setupEditor = function(dom, attr) {
            var editor = $(".bennu-html-code-editor", dom).data("editor");
            if (editor) {
                return editor;
            }
            Bennu.codeEditor.require();
            editor = ace.edit($(".bennu-html-code-editor", dom)[0])
            editor.setFontSize(13);
            editor.setTheme("ace/theme/clouds");
            editor.getSession().setMode("ace/mode/html");
            editor.setHighlightActiveLine(false);
            editor.setShowPrintMargin(false);
            editor.getSession().setUseWrapMode(true);
            $(".bennu-html-code-editor", dom).data("editor",editor);

            editor.on("input",function(){
                if (attr !== null && attr !== undefined) {
                    var data = JSON.parse(handler.get());
                    var locale = $(".bennu-localized-string-language", dom).data("locale");
                    var tag = locale.tag;

                    if (!(tag in data)){
                        var singleTag = locale.tag.split("-")[0].toLowerCase();
                        if (singleTag in data){
                            tag = singleTag;
                        }
                    }

                    data[tag] = editor.getValue();
                    handler.set(JSON.stringify(data));
                }else{
                    handler.set(editor.getValue());
                }
            });

            return editor;
        }

        if ($(".bennu-html-code-editor", dom).length != 0){
            dom.data("showSrc", false);
            var attr = e.attr("bennu-localized-string");

            $(".switch-to-code", dom).on("click", function(){
                var editor = setupEditor(dom, attr);
                if (!dom.data("showSrc")){
                    $(".bennu-html-editor-toolbar .btn", dom).map(function (){ (!$(this).hasClass("switch-to-code")) && $(this).attr("disabled", ""); })

                    $(".switch-to-code", dom).addClass("active btn-primary");

                    $(".bennu-html-editor-editor", dom).hide();
                    $(".bennu-html-code-editor-container", dom).show();
                    $(".bennu-localized-string-button", dom).attr("disabled","");
                    if (attr !== null && attr !== undefined) {
                        var data = JSON.parse(handler.get());
                        var locale = $(".bennu-localized-string-language", dom).data("locale");
                        var tag = locale.tag;

                        if (!(tag in data)){
                            var singleTag = locale.tag.split("-")[0].toLowerCase();
                            if (singleTag in data){
                                tag = singleTag;
                            }
                        }

                        var t = data[tag]

                        editor.setValue(t);
                    }else{
                        editor.setValue();
                    }

                    dom.data("showSrc",true);
                }else{
                    $(".bennu-html-editor-toolbar .btn", dom).map(function (){ $(this).removeAttr("disabled"); })

                    $(".switch-to-code", dom).removeClass("active btn-primary");
                    $(".bennu-localized-string-button", dom).removeAttr("disabled");
                    $(".bennu-html-editor-editor", dom).show();
                    $(".bennu-html-code-editor-container", dom).hide();
                    //

                    if (attr !== null && attr !== undefined) {
                        var data = JSON.parse(handler.get());
                        var locale = $(".bennu-localized-string-language", dom).data("locale");
                        var tag = locale.tag;

                        if (!(tag in data)){
                            var singleTag = locale.tag.split("-")[0].toLowerCase();
                            if (singleTag in data){
                                tag = singleTag;
                            }
                        }

                        data[tag] = editor.getValue();
                        handler.set(JSON.stringify(data));
                    }else{
                        handler.set(editor.getValue());
                    }
                    dom.data("showSrc",false);
                }
            });
        }

        var changeColor = function(json){
            if(json.backColor === "inherit" && json.foreColor === "inherit"){
                document.execCommand("removeFormat",false);
            }else{
                document.execCommand("backColor",false,json.backColor);
                if (json.foreColor === "inherit"){
                    document.execCommand("foreColor",false,"#444");
                }else{
                    document.execCommand("foreColor",false,json.foreColor);
                }
            }
            
        }

        if ($(".note-color", dom).length != 0){
            $("[data-event='backColor']").map(function(){
                $(this).on("click",function(ev){
                    var et = $(ev.target);
                    var col = et.data("value")
                    var json = $(".note-recent-color", dom).data("value");
                    json['backColor'] = col;
                    $(".note-recent-color i", dom).css("background-color",col);
                    $(".note-recent-color", dom).data("value",json);
                    changeColor(json)
                });
            });

            $("[data-event='foreColor']").map(function(){
                $(this).on("click",function(ev){
                    var et = $(ev.target);
                    $(".bennu-html-editor-editor", dom).focus();
                    var col = et.data("value")
                    var json = $(".note-recent-color", dom).data("value");
                    json['foreColor'] = col;
                    $(".note-recent-color i", dom).css("color",col);
                    $(".note-recent-color", dom).data("value",json);
                    changeColor(json);
                });
            });

            $(".note-recent-color",dom).on("click",function(){
                changeColor($(".note-recent-color",dom).data("value"));
            });
        }



        return handler;
    };
})();
