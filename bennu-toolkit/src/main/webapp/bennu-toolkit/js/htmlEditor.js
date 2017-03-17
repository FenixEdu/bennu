/*
 * htmlEditor.js
 * 
 * Copyright (c) 2014, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of Bennu Toolkit.
 * 
 * Bennu Toolkit is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Bennu Toolkit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Bennu Toolkit. If not, see
 * <http://www.gnu.org/licenses/>.
 */

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

    Bennu.htmlEditor.components = Bennu.htmlEditor.components || {};

    Bennu.htmlEditor.components.get = function(k){
        if(Bennu.htmlEditor.components.list){
            for (var i = 0; i < Bennu.htmlEditor.components.list.length; i++) {
                if(Bennu.htmlEditor.components.list[i].key === k){
                    return Bennu.htmlEditor.components.list[i];
                }
            };
        }
    }

    Bennu.htmlEditor.components.init = function(editor){
        Bennu.htmlEditor.components.list = [];
        Bennu.htmlEditor.components.provided = {};
        $.get(Bennu.contextPath + "/api/bennu-toolkit/components",function(json){
            for (var i = 0; i < json.length; i++) {
                var component = json[i];

                if(!component.order){
                    component.order = 0;
                }

                Bennu.htmlEditor.components.list.push(component);
            };
            Bennu.htmlEditor.components.reflow(editor);
        });

        Bennu.htmlEditor.components.attachModal();
    };

    Bennu.htmlEditor.components.attachModal = function(){
        if(!$("#bennu-html-editor-component-modal").length){
            var template ='<div class="modal fade" id="bennu-html-editor-component-modal">'+
                '<div class="modal-dialog">'+
                    '<div class="modal-content">'+
                        '<div class="modal-header">'+
                            '<button type="button" class="close" data-dismiss="modal"><span class="sr-only">Close</span></button>'+
                            '<h3 class="modal-title"></h3>'+
                            '<small></small>'+
                        '</div>'+
                        '<div class="modal-body">'+

                        '</div>'+
                        '<div class="modal-footer">'+
                            '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>'+
                            '<button type="button" class="btn btn-primary"></button>'+
                        '</div>'+
                    '</div><!-- /.modal-content -->'+
                '</div><!-- /.modal-dialog -->'+
            '</div><!-- /.modal -->'
            $(document.body).append(template);
        }
    }

    Bennu.htmlEditor.components.showModal = function(editor){
        $("#bennu-html-editor-component-modal").data("editor",editor)
        $("#bennu-html-editor-component-modal").modal("show")
    };

    Bennu.htmlEditor.components.hideModal = function(){
        $("#bennu-html-editor-component-modal").modal("hide")
        Bennu.htmlEditor.components.reflow($("#bennu-html-editor-component-modal").data("editor"));
        $("#bennu-html-editor-component-modal").data("editor",null);
    };

    Bennu.htmlEditor.components.setTitle = function(t){
        $("#bennu-html-editor-component-modal .modal-header h3").html(t);
    };

    Bennu.htmlEditor.components.setSubtitle = function(t){
        $("#bennu-html-editor-component-modal .modal-header small").html(t);
    };

    Bennu.htmlEditor.components.setBody = function(t){
        $("#bennu-html-editor-component-modal .modal-body").html(t);
    };

    Bennu.htmlEditor.components.hidePrimaryButton = function(){
        $("#bennu-html-editor-component-modal .modal-footer button.btn-primary").hide();
    };

    Bennu.htmlEditor.components.showPrimaryButton = function(){
        $("#bennu-html-editor-component-modal .modal-footer button.btn-primary").show();
    };

    Bennu.htmlEditor.components.setPrimaryButton = function(t){
        $("#bennu-html-editor-component-modal .modal-footer button.btn-primary").html(t);
    };

    Bennu.htmlEditor.components.clickPrimaryButton = function(fn){
        $("#bennu-html-editor-component-modal .modal-footer button.btn-primary").off("click.components").on("click.components",fn);
    }

    Bennu.htmlEditor.components.callback = {};
    Bennu.htmlEditor.components.preview = {};
    Bennu.htmlEditor.components.menu = {};

    function isNumeric(n) {
        return !isNaN(parseFloat(n)) && isFinite(n);
    }

    Bennu.htmlEditor.components.mkHandler = function(editor,element){
        return {
            element:element,
            editor : editor,
            selection : Bennu.htmlEditor.saveSelection(),
            files:function(files,cb){
                this.editor.closest(".bennu-html-editor-input").data("related").data("fileHandler")(files,cb);
            },
            text: function(x){
                if (x === undefined){
                    var html = "";
                    if (this.element){
                        return this.element.html();
                    }else if (typeof window.getSelection != "undefined") {
                        var sel = this.selection;
                        if (sel.cloneContents) {
                            html = sel.cloneContents().textContent;
                        }
                    } else if (typeof document.selection != "undefined") {
                        if (document.selection.type == "Text") {
                            html = document.selection.createRange().htmlText;
                        }
                    }
                    return html;
                }else{
                    var sel, range;
                    if (this.element){
                        var newEl = this.element.replaceWith(x);

                        var r = document.createRange();
                        r.selectNodeContents(newEl[0]);

                        var sel=window.getSelection();
                        sel.removeAllRanges();
                        sel.addRange(r);
                        this.selection = sel;

                    }else if (window.getSelection) {
                        if (this.selection && $(this.selection.commonAncestorContainer).closest(".bennu-html-editor-editor")[0] == this.editor[0]){
                          this.selection.deleteContents();
                        }else{
                          this.editor.focus()
                          this.selection = document.createRange();
                          this.selection.setStart(this.editor[0],0)
                          this.selection.setEnd(this.editor[0],0)
                        }

                        var jq = $(x);
                        var that = this;

                        // first asdd
                        jq = $.map(jq,function(e){
                          var z = $(e);
                          that.selection.insertNode(z[0]);

                          return z;
                        });

                        //r.selectNodeContents();
                        var sel=window.getSelection();
                        sel.removeAllRanges();

                        // then select
                        var selected = false
                        $.map(jq,function(e){
                          var r = document.createRange();
                          r.selectNode(e[0]);
                          sel.addRange(r);
                        });

                        this.selection = r;
                    } else if (document.selection && document.selection.createRange) {
                        range = document.selection.createRange();
                        range.text = x;
                    }
                    return x;
                }
            },
            restore : function () {
                $(editor).focus();
                Bennu.htmlEditor.restoreSelection(this.selection);
            }
        }
    }

    Bennu.htmlEditor.components.firstStep = function(editor){
        var handler = Bennu.htmlEditor.components.mkHandler(editor);
        Bennu.htmlEditor.components.clearModal();
        Bennu.htmlEditor.components.setTitle("Add New Component");
        Bennu.htmlEditor.components.setSubtitle("Choose something to add to your content");

        var output = "";
        output += '<div class="panel panel-default">'+
        '<ul class="list-group">';

        for (var i = 0; i < Bennu.htmlEditor.components.list.length; i++) {
            var c = Bennu.htmlEditor.components.list[i]
            output += '<li class="list-group-item">' +
            '<h4><a data-type="' + c.key + '" href="#">' + c.name + '</a></h4>'+
            '<p>' + c.description + '</p>'+
            '</li>'
        };

        output += '</ul>'+
        '</div>';

        output = $(output);

        $("a", output).on("click",function(e){
            var key = $(e.target).data("type");

            Bennu.htmlEditor.components.showEditorFor(key,null,handler);
        });

        Bennu.htmlEditor.components.setBody(output);
        Bennu.htmlEditor.components.hidePrimaryButton();
        Bennu.htmlEditor.components.showModal(editor);
    };

    Bennu.htmlEditor.components.showEditorFor = function(key,element,handler){
        var c = Bennu.htmlEditor.components.get(key);

        var files = $.map(c.files,function(e){
            return Bennu.contextPath + e;
        });

        if (Bennu.ensure.isLoaded(files)){
            Bennu.htmlEditor.components.callback[c.key](element,handler);
        }else{
            Bennu.htmlEditor.components.clearModal();
            Bennu.htmlEditor.components.setTitle("Loading...");
            Bennu.htmlEditor.components.setSubtitle("We are calling home");
            Bennu.htmlEditor.components.setBody("<div style='text-align:center;'>" +
                "<i bennu-spinner color='primary'></i>"+
            "</div>");
            Bennu.htmlEditor.components.hidePrimaryButton();

            Bennu.ensure(files,function(){
                Bennu.htmlEditor.components.clearModal();
                Bennu.htmlEditor.components.callback[c.key](element,handler);
            });
        }
    }

    Bennu.htmlEditor.components.reflow = function(editor){
        $("[bennu-component]",editor)
        .off("dblclick.bennu-component").each(function(i,e){
            e=$(e);
            var context = e.data("context");
            if (context) {
                context.destroy();
            };
        }).on("dblclick.bennu-component", function(e){
            e=$(e.target);
            e=e.closest("[bennu-component]");
            Bennu.htmlEditor.components.showModal(editor);
            var handler = Bennu.htmlEditor.components.mkHandler(editor,e);
            Bennu.htmlEditor.components.showEditorFor(e.attr("bennu-component"),e, handler);
        }).each(function(i,element){
            element = $(element)
            var key = element.attr("bennu-component");

            var c = Bennu.htmlEditor.components.get(key);

            var files = $.map(c.files,function(e){
                return Bennu.contextPath + e;
            });

            Bennu.ensure(files,function(){
                element.removeAttr("style");
                element.removeAttr("class");
                var x = Bennu.htmlEditor.components.preview[key];

                x && x(element);
                element.contextmenu({
                    target: $(".bennu-html-editor-context-menu", editor.closest(".bennu-html-editor-input")),
                    before: function (e) {
                      // This function is optional.
                      // Here we use it to stop the event if the user clicks a span
                      this.getMenu().find("ul").empty();
                      e.preventDefault();
                      var menu = Bennu.htmlEditor.components.menu[key]
                      var that = this;
                      menu && menu(element, {
                        add:function(el){
                            that.getMenu().find("ul").append(el);
                        },
                        target:function(){
                            return $(e.target)
                        },
                        closeMenu:function(){
                            that.closemenu(e);
                        }
                      });
                      var edit = $("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Edit</a></li>")
                      edit.on("click",function(){
                        that.closemenu(e);
                        Bennu.htmlEditor.components.showModal(editor);
                        var handler = Bennu.htmlEditor.components.mkHandler(editor,element);
                        Bennu.htmlEditor.components.showEditorFor(element.attr("bennu-component"),element,handler);
                      });

                      that.getMenu().find("ul").append(edit);
                      var del = $("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-trash'></i> Delete</a></li>")
                      del.on("click",function(){
                        that.closemenu(e);
                        element.remove()
                      });
                      that.getMenu().find("ul").append(del);
                      return true;
                    }
                });
            });
        });
        var components = $("[bennu-component]",editor)
        if(components.length > 0){
            components.each(function(i,e){
                e=$(e);
                e=e.closest("[bennu-component]");
            });
        }
    };


    Bennu.htmlEditor.components.clearModal = function(){
        var modal = $("#bennu-html-editor-component-modal");
        $(".modal-body", modal).empty();
        $(".modal-header h3", modal).html("");
        $(".modal-header small", modal).html("");
        $(".modal-footer button.btn-primary", modal).html("");
        $("#bennu-html-editor-component-modal .modal-footer button.btn-primary").off("click")
    };

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
        e = $(e);
        var handler = Bennu.widgetHandler.makeFor(e);

        Bennu.ensure([
            Bennu.contextPath + "/bennu-toolkit/js/libs/bootstrap-wysiwyg.js",
            Bennu.contextPath + "/bennu-toolkit/js/libs/jquery.fullscreen.js",
            Bennu.contextPath + "/bennu-toolkit/js/libs/sanitize.js",
            Bennu.contextPath + "/bennu-toolkit/js/libs/jquery.hotkeys.js",
            Bennu.contextPath + "/bennu-toolkit/js/libs/bootstrap-contextmenu.js"
        ], function(){
        var dom = $('<div class="bennu-html-editor-input">' +
            '<div class="bennu-html-editor-tools">' +
            '<div class="bennu-html-editor-toolbar btn-toolbar" data-role="editor-toolbar" data-target=".bennu-html-editor-editor"></div>' +
            '</div>' +
            '<div class="bennu-html-editor-editor form-control editor-input" contenteditable="true"></div>' +
            '<span class="help-block"></span>' +
                '<div class="bennu-html-editor-context-menu">'+
                    '<ul class="dropdown-menu" role="menu">'+
                    '</ul>'+
                '</div>'+
            '</div>');
        var toolbarReqs = "size,style,lists,align,colors,links,table,image,components,undo,fullscreen,source";
        if (Bennu.utils.hasAttr(e,"toolbar")) {
            toolbarReqs = e.attr("toolbar");
        }

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
                    '<a class="btn btn-sm btn-default link-to-add-btn dropdown-toggle" data-toggle="dropdown" title="" data-original-title="Hyperlink"><span class="glyphicon glyphicon-link"></span></a>' +
                    '<a class="btn btn-sm btn-default" data-edit="unlink" title="" data-original-title="Remove Hyperlink"><span class="fa fa-chain-broken"></span></a>' +
                    '</div>');
            } else if (c === "table"){
                $(".btn-toolbar", dom).append('<div class="btn-group">' +
                    '<button type="button" class="btn table-btn btn-default btn-sm btn-small dropdown-toggle" data-original-title="Table"><i class="fa fa-table icon-table"></i></button>'+
                    '</div>');
            } else if (c === "components"){
                 $(".btn-toolbar", dom).append('<div class="btn-group">'+
                    '<button type="button" class="btn btn-default btn-sm btn-small bennu-html-editor-components-button" title="" tabindex="-1" data-original-title="Components">'+
                        '<i class="fa fa-puzzle-piece" ></i>'+
                    '</button>'+
                    '</div>');
            } else if (c === "colors"){
                $(".btn-toolbar", dom).append('<div class="btn-group">'+
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

            // TODO: attach this to the warning system
            $('<div class="alert"> <button type="button" class="close" data-dismiss="alert">&times;</button>' +
                '<strong>File upload error</strong> ' + msg + ' </div>').prependTo('#alerts');
        }

        // HTML Sanitization Rules
        var s = new Sanitize({
          elements: [
            'a', 'b', 'blockquote', 'br', 'caption', 'cite', 'code', 'col',
            'colgroup', 'dd', 'dl', 'dt', 'em', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
            'i', 'img', 'li', 'ol', 'p', 'pre', 'q', 'small', 'strike', 'strong',
            'sub', 'sup', 'table', 'tbody', 'td', 'tfoot', 'th', 'thead', 'tr', 'u',
            'ul'],

          attributes: {
            'a'         : ['href', 'title'],
            'blockquote': ['cite'],
            'col'       : ['span', 'width'],
            'colgroup'  : ['span', 'width'],
            'img'       : ['align', 'alt', 'height', 'src', 'title', 'width'],
            'ol'        : ['start', 'type'],
            'q'         : ['cite'],
            'table'     : ['summary', 'width'],
            'td'        : ['abbr', 'axis', 'colspan', 'rowspan', 'width'],
            'th'        : ['abbr', 'axis', 'colspan', 'rowspan', 'scope',
                             'width'],
            'ul'        : ['type'],
            '__ALL__'   : ["bennu-component", "data-height","data-x","data-y", "data-width", "data-source", "data-metadata",]
          },

          protocols: {
            'a'         : {'href': ['ftp', 'http', 'https', 'mailto',
                                        Sanitize.RELATIVE]},
            'blockquote': {'cite': ['http', 'https', Sanitize.RELATIVE]},
            'img'       : {'src' : ['http', 'https', Sanitize.RELATIVE]},
            'q'         : {'cite': ['http', 'https', Sanitize.RELATIVE]}
          }
        });

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

        if (Bennu.utils.hasAttr(e,"bennu-localized-string")) {
            var menu = $('<div class="btn-group bennu-localized-string-group">' +
                '<button type="button" class="btn btn-default dropdown-toggle bennu-localized-string-button" data-toggle="dropdown">' +
                '<span class="bennu-localized-string-language"></span> <span class="caret"></span>' +
                '</button>' +
                '<ul class="dropdown-menu bennu-localized-string-menu" role="menu"></ul></div>');
            $("div.btn-toolbar", dom).before(menu);
            dom.data("localized-string", true);

            Bennu.localizedString.makeLocaleList($(".bennu-localized-string-menu", dom), dom, function (e) {
                Bennu.localizedString.changeData($(e.target).parent().data("locale"), $(".bennu-localized-string-language", dom), widgetInput, dom);
                Bennu.htmlEditor.components.reflow($(".bennu-html-editor-editor", dom));
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

            Bennu.htmlEditor.components.init($(".bennu-html-editor-editor",dom));

        if ($(".bennu-html-editor-components-button", dom).length){
            var editor = $(".bennu-html-editor-editor",dom);
            $(".bennu-html-editor-components-button", dom).on("click",function(){
                Bennu.htmlEditor.components.firstStep(editor);
            });
        }

        $(".bennu-html-editor-editor", dom).on('change', function () {
            if (Bennu.utils.hasAttr(e,"bennu-localized-string")) {
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
          var editor = $(".bennu-html-editor-editor", dom);
          var handler = Bennu.htmlEditor.components.mkHandler(editor);
          Bennu.htmlEditor.components.showModal(editor);
          Bennu.htmlEditor.components.showEditorFor("image",null,handler);
        });

        var attachPicture = function(urls){
          var c = Bennu.htmlEditor.components.get("image");

          var files = $.map(c.files,function(e){
              return Bennu.contextPath + e;
          });

          Bennu.ensure(files, function(){
            var editor = $(".bennu-html-editor-editor", dom);

            var handler = editor.data("handlerTMP");
            editor.data("handlerTMP",null);
            handler.restore();

            if (urls.length == 1){
                Bennu.htmlEditor.components.callback.image.renderer(urls[0],function(e){
                    Bennu.htmlEditor.components.showModal(editor);
                    Bennu.htmlEditor.components.showEditorFor("image",e,handler);
                });
            }else{
                var arr = [];

                for (var i = 0; i < urls.length; i++) {
                    var o = urls[i];
                    arr.push(Bennu.htmlEditor.components.callback.image.renderer(o));
                }
                handler.text(arr);
                Bennu.htmlEditor.components.reflow(editor);
            }
          });

        };


        dom.on('dragenter dragover', false)
            .on('drop', function (evt) {
                var editor = $(".bennu-html-editor-editor", dom);
                editor.focus();
                var handler = Bennu.htmlEditor.components.mkHandler(editor);

                editor.data("handlerTMP",handler);

                var dataTransfer = evt.originalEvent.dataTransfer;
                evt.stopPropagation();
                evt.preventDefault();
                if (dataTransfer && dataTransfer.files && dataTransfer.files.length > 0) {
                    var z = e.data("fileHandler")
                    z && z(dataTransfer.files, attachPicture);
                }
            });
        $('.bennu-html-editor-editor', dom).wysiwyg({ dragAndDropImages: false, fileUploadError: showErrorAlert});

        
        
        e.after(dom);
        Bennu.validation.attachToForm(dom);
        Bennu.utils.replaceRequired(e);

        $(".table-btn",dom).on("click",function(){
            var editor = $(".bennu-html-editor-editor", dom);
            var handler = Bennu.htmlEditor.components.mkHandler(editor);
            Bennu.htmlEditor.components.showModal(editor);
            Bennu.htmlEditor.components.showEditorFor("table",null,handler);
        })

        $(".link-to-add-btn", dom).on("click",function(){
            var editor = $(".bennu-html-editor-editor", dom);
            var handler = Bennu.htmlEditor.components.mkHandler(editor);
            Bennu.htmlEditor.components.showModal(editor);
            Bennu.htmlEditor.components.showEditorFor("link",null,handler);
        });

        e.on("change.bennu", function (ev) {
            if (Bennu.utils.hasAttr(e,"bennu-localized-string")) {
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

        if (!e.data("fileHandler")){
          e.data("fileHandler",function(files,cb){
            var result = []
            var total = files.length;
            var proc = 0;

            $.map(files, function(f,i){
              var fileReader = new FileReader(f);
              fileReader.onload = function (e) {
                 result[i] = e.target.result;
                 proc++;
                 if (proc == total){
                   cb(result);
                 }
              }
              fileReader.readAsDataURL(f);
            });
          });
        }

        var setupEditor = function(dom, cb) {
            var editor = $(".bennu-html-code-editor", dom).data("editor");
            if (editor) {
                cb(editor);
                return;
            }
            Bennu.codeEditor.require(function () {
                editor = ace.edit($(".bennu-html-code-editor", dom)[0])
                editor.setFontSize(13);
                editor.setTheme("ace/theme/clouds");
                editor.getSession().setMode("ace/mode/html");
                editor.setHighlightActiveLine(false);
                editor.setShowPrintMargin(false);
                editor.getSession().setUseWrapMode(true);
                $(".bennu-html-code-editor", dom).data("editor",editor);

                editor.on("input",function(){
                    if (Bennu.utils.hasAttr(dom.data("related"), "bennu-localized-string")) {
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
                cb(editor);
            });
        }

        if ($(".bennu-html-code-editor", dom).length != 0){
            dom.data("showSrc", false);
            $(".switch-to-code", dom).on("click", function(){
                setupEditor(dom, function(editor) {
                    if (!dom.data("showSrc")){
                        $(".bennu-html-editor-toolbar .btn", dom).map(function (){ (!$(this).hasClass("switch-to-code")) && $(this).attr("disabled", ""); })

                        $(".switch-to-code", dom).addClass("active btn-primary");

                        $(".bennu-html-editor-editor", dom).hide();
                        $(".bennu-html-code-editor-container", dom).show();
                        $(".bennu-localized-string-button", dom).attr("disabled","");
                        if (Bennu.utils.hasAttr(e,"bennu-localized-string")) {
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

                        if (Bennu.utils.hasAttr(e,"bennu-localized-string")) {
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



        
    });
    return handler;
    };
})();
