(function () {
    /** @const */ var ALLOW_VOICE = false;
    /** @const */ var ALLOW_FULLSCREEN = true;


    Bennu.htmlEditor = Bennu.htmlEditor || {};
    Bennu.htmlEditor.attr = "bennu-html-editor";

    Bennu.htmlEditor.fullscreen = function (e) {
        var target = $(e.target);
        a = target.closest(".bennu-html-editor-input");
        a.toggleFullScreen();

        a.addClass("fullscreen");
        $(".fullscreen-button", a).addClass("btn-primary");
        $(document).bind("fullscreenchange", function () {
            var state = ($(document).fullScreen() ? "on" : "off");
            if (state == "off") {
                $(".fullscreen").removeClass("fullscreen");
                $(".fullscreen-button", a).removeClass("btn-primary");
                $(".bennu-html-editor-tools", a).off("mouseover mouseout");
                a.removeClass("visible");
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
            '<div class="bennu-html-editor-editor editor-input" contenteditable="true"></div>' +
            '<span class="help-block"></span>' +
            '</div>');

        var toolbarReqs = e.attr("toolbar");
        if (toolbarReqs === "" || toolbarReqs === undefined || toolbarReqs === null) {
            toolbarReqs = "size,style,lists,align,links,image,undo,fullscreen";
        }
        $()
        toolbarReqs = toolbarReqs.split(",");

        for (var i = 0; i < toolbarReqs.length; i++) {
            var c = toolbarReqs[i];
            if (c === "size") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Font Size">' +
                    '<span class="glyphicon glyphicon-text-height"></span>&nbsp;<b class="caret"></b></a>' +
                    '<ul class="dropdown-menu">' +
                    '<li><a data-edit="fontSize 5"><font size="5">Huge</font></a></li>' +
                    '<li><a data-edit="fontSize 3"><font size="3">Normal</font></a></li>' +
                    '<li><a data-edit="fontSize 1"><font size="1">Small</font></a></li>' +
                    '</ul></div>');
            } else if (c === "style") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="bold" title="" data-original-title="Bold (Ctrl/Cmd+B)"><span class="glyphicon glyphicon-bold"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="italic" title="" data-original-title="Italic (Ctrl/Cmd+I)"><span class="glyphicon glyphicon-italic"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="strikethrough" title="" data-original-title="Strikethrough"><span class="fa fa-strikethrough"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="underline" title="" data-original-title="Underline (Ctrl/Cmd+U)"><span class="fa fa-underline"></span></a>' +
                    '</div>');
            } else if (c === "lists") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="insertunorderedlist" title="" data-original-title="Bullet list"><span class="fa fa-list-ul"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="insertorderedlist" title="" data-original-title="Number list"><span class="fa fa-list-ol"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="outdent" title="" data-original-title="Reduce indent (Shift+Tab)"><span class="glyphicon glyphicon-indent-left"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="indent" title="" data-original-title="Indent (Tab)"><span class="glyphicon glyphicon-indent-right"></span></a>' +
                    '</div>');
            } else if (c === "align") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default btn-primary" data-edit="justifyleft" title="" data-original-title="Align Left (Ctrl/Cmd+L)"><span class="glyphicon glyphicon-align-left"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifycenter" title="" data-original-title="Center (Ctrl/Cmd+E)"><span class="glyphicon glyphicon-align-center"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifyright" title="" data-original-title="Align Right (Ctrl/Cmd+R)"><span class="glyphicon glyphicon-align-right"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="justifyfull" title="" data-original-title="Justify (Ctrl/Cmd+J)"><span class="glyphicon glyphicon-align-justify"></span></a>' +
                    '</div>');
            } else if (c === "links") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Hyperlink"><span class="glyphicon glyphicon-link"></span></a>' +
                    '<div class="dropdown-menu input-append">' +
                    '<div class="input-group"><input class="form-control" placeholder="URL" type="text" data-edit="createLink">' +
                    '<button class="btn btn-sm btn-default" type="button">Add</button></div>' +
                    '</div>' +
                    '<a class="btn btn-sm btn-default" data-edit="unlink" title="" data-original-title="Remove Hyperlink"><span class="fa fa-chain-broken"></span></a>' +
                    '</div>');
            } else if (c === "image") {
                $(".btn-toolbar", dom).append(
                        '<a class="pictureBtn btn btn-sm btn-default" title="" data-original-title="Insert picture (or just drag &amp; drop)"><span class="glyphicon glyphicon-picture"></span></a>' +
                        '<input type="file" name="pictureTlb" style="position:absolute; top: -1000px;" multiple>');
            } else if (c === "undo") {
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<a class="btn btn-sm btn-default" data-edit="undo" title="" data-original-title="Undo (Ctrl/Cmd+Z)"><span class="fa fa-undo"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="redo" title="" data-original-title="Redo (Ctrl/Cmd+Y)"><span class="fa fa-repeat"></span></a>' +
                    '</div>');
            } else if (c == "voice" && 'webkitSpeechRecognition' in window && ALLOW_VOICE) {
                $(".btn-toolbar", dom).append('<button class="voiceBtn" ><img alt="Start" class="voiceBtnImage"src="' + Bennu.contextPath + '/static/img/mic.gif"></button>');
            } else if (c === "fullscreen" && ALLOW_FULLSCREEN) {
                $(".btn-toolbar", dom).append('<a href="#" data-original-title="Enter Zen Mode" class="btn btn-sm btn-default fullscreen-button"><span class="fa fa-arrows-alt"></span></a>');
                $(".fullscreen-button", dom).on("click", Bennu.htmlEditor.fullscreen);
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

        if ('webkitSpeechRecognition' in window && ALLOW_VOICE) {
            var editorOffset = $('.bennu-html-editor-editor', dom).offset();
//                $('.voiceBtn', dom).css('position', 'absolute').offset({top: editorOffset.top + 2, left: editorOffset.left + $('.bennu-html-editor-editor', dom).innerWidth() - 50})
            var start_img = $('.voiceBtn .voiceBtnImage', dom)[0];
            var recognition = new webkitSpeechRecognition();
            var recognizing = false;
            var final_span, interim_span;
            recognition.continuous = true;
            recognition.interimResults = true;

            recognition.onstart = function () {
                recognizing = true;
                start_img.src = '' + Bennu.contextPath + '/static/img/mic-an.gif';
                $(".bennu-html-editor-editor", dom).append('<span class="final" id="final_span"></span> <span class="interim" id="interim_span"></span>')

                function replaceSelection(html, selectInserted) {
                    var sel, range, fragment;

                    if (typeof window.getSelection != "undefined") {
                        // IE 9 and other non-IE browsers
                        sel = window.getSelection();

                        // Test that the Selection object contains at least one Range
                        if (sel.getRangeAt && sel.rangeCount) {
                            // Get the first Range (only Firefox supports more than one)
                            range = window.getSelection().getRangeAt(0);
                            range.deleteContents();

                            // Create a DocumentFragment to insert and populate it with HTML
                            // Need to test for the existence of range.createContextualFragment
                            // because it's non-standard and IE 9 does not support it
                            if (range.createContextualFragment) {
                                fragment = range.createContextualFragment(html);
                            } else {
                                // In IE 9 we need to use innerHTML of a temporary element
                                var div = document.createElement("div"), child;
                                div.innerHTML = html;
                                fragment = document.createDocumentFragment();
                                while ((child = div.firstChild)) {
                                    fragment.appendChild(child);
                                }
                            }
                            var firstInsertedNode = fragment.firstChild;
                            var lastInsertedNode = fragment.lastChild;
                            range.insertNode(fragment);
                            if (selectInserted) {
                                if (firstInsertedNode) {
                                    range.setStartBefore(firstInsertedNode);
                                    range.setEndAfter(lastInsertedNode);
                                }
                                sel.removeAllRanges();
                                sel.addRange(range);
                            }
                        }
                    } else if (document.selection && document.selection.type != "Control") {
                        // IE 8 and below
                        range = document.selection.createRange();
                        range.pasteHTML(html);
                    }
                }

                replaceSelection('<span class="final" id="final_span"></span> <span class="interim" id="interim_span"></span>', false)
                final_span = $("#final_span", dom)[0]
                interim_span = $("#interim_span", dom)[0]

            };

            recognition.onerror = function (event) {
                if (event.error == 'no-speech') {
                    start_img.src = '' + Bennu.contextPath + '/static/img/mic.gif';
                    //showInfo('info_no_speech');
                    //ignore_onend = true;
                }
                if (event.error == 'audio-capture') {
                    start_img.src = '' + Bennu.contextPath + '/static/img/mic.gif';
                    //showInfo('info_no_microphone');
                    //ignore_onend = true;
                }
                if (event.error == 'not-allowed') {
                    if (event.timeStamp - start_timestamp < 100) {
                        //showInfo('info_blocked');
                    } else {
                        //showInfo('info_denied');
                    }
                    //ignore_onend = true;
                }
            };

            recognition.onend = function () {
                recognizing = false;
                if (ignore_onend) {
                    return;
                }
                start_img.src = '' + Bennu.contextPath + '/static/img/mic.gif';
                if (!final_transcript) {
                    //showInfo('info_start');
                    return;
                }
                //showInfo('');
                if (window.getSelection) {
                    window.getSelection().removeAllRanges();
                    var range = document.createRange();
                    range.selectNode(document.getElementById('final_span'));
                    window.getSelection().addRange(range);
                }
            };

            recognition.onresult = function (event) {
                var interim_transcript = '';
                if (typeof(event.results) == 'undefined') {
                    recognition.onend = null;
                    recognition.stop();
                    upgrade();
                    return;
                }
                for (var i = event.resultIndex; i < event.results.length; ++i) {
                    if (event.results[i].isFinal) {
                        final_transcript += event.results[i][0].transcript;
                    } else {
                        interim_transcript += event.results[i][0].transcript;
                    }
                }
                final_transcript = capitalize(final_transcript);
                final_span.innerHTML = linebreak(final_transcript);
                interim_span.innerHTML = linebreak(interim_transcript);
                if (final_transcript || interim_transcript) {
                    //showButtons('inline-block');
                }
            };

            $('.voiceBtn', dom).on("click", function (event) {
                event.preventDefault();
                if (recognizing) {
                    recognition.stop();
                    return;
                }

                final_transcript = '';
                recognition.lang = "pt-PT"
                recognition.start();
                ignore_onend = false;
                start_img.src = '' + Bennu.contextPath + '/static/img/mic-slash.gif';
                //showInfo('info_allow');
                //showButtons('none');
                start_timestamp = event.timeStamp;
            });
        }

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
        }

        $(".bennu-html-editor-editor", dom).on('change', function () {
            var attr = e.attr("bennu-localized-string");
            if (attr !== null && attr !== undefined) {
                var data = JSON.parse($(dom.data("related")).val());
                data[$(".bennu-localized-string-language", dom).data("locale").tag] = widgetInput.val();
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

        function submitFiles(files) {
            var formData = new FormData();
            for (var i = 0; i < files.length; i++) {
                formData.append('attachment', files[i]);
            }


            var xhr = new XMLHttpRequest();
            xhr.open('POST', 'addFile.json');

            function transferCanceled(event) {

            }

            function transferFailed(event) {

            }

            function transferComplete(event) {
                var objs = JSON.parse(event.currentTarget.response);
                $(".bennu-html-editor-editor", dom).focus()
                for (var i = 0; i < objs.length; i++) {
                    var o = objs[i];
                    document.execCommand('insertimage', 0, o.url);
                }
            }

            function updateProgress(event) {
                if (event.lengthComputable) {
                    var complete = (event.loaded / event.total * 100 | 0);
                    //progress.value = progress.innerHTML = complete;
                    console.log(complete);
                }
            }

            xhr.addEventListener("progress", updateProgress, false);
            xhr.addEventListener("load", transferComplete, false);
            xhr.addEventListener("error", transferFailed, false);
            xhr.addEventListener("abort", transferCanceled, false);

            xhr.send(formData);
        }

        $(".btn-toolbar .pictureBtn", dom).on("click", function () {
            $(".btn-toolbar input[name='pictureTlb']", dom).click();
        });

        $(".btn-toolbar input[name='pictureTlb']", dom).on("change", function (evt) {
            submitFiles($(".btn-toolbar input[name='pictureTlb']", dom)[0].files);
        });

        dom.on('dragenter dragover', false)
            .on('drop', function (e) {
                var dataTransfer = e.originalEvent.dataTransfer;
                e.stopPropagation();
                e.preventDefault();
                if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
                    submitFiles(dataTransfer.files);
                }
            });
        $('.bennu-html-editor-editor', dom).wysiwyg({ dragAndDropImages: false, fileUploadError: showErrorAlert});

        Bennu.validation.attachToForm(dom);
        e.after(dom);

        e.on("change.bennu", function (ev) {
            var t = $(e).val()
            var r = $(".bennu-html-editor-editor", dom).html();
            if (r !== t){
                $(".bennu-html-editor-editor", dom)[0].innerHTML = t;
                e.data("handler").trigger();
            }
        });

        return Bennu.widgetHandler.makeFor(e);
    };
})();
