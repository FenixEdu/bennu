/*
 * codeEditor.js
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

(function(){
  	Bennu.codeEditor = Bennu.codeEditor || {};
  	Bennu.codeEditor.attr = "bennu-code-editor";

	var modalTemplate = ""+
		"<div class=\"modal fade\" id=\"bennu-code-editor-modal\">"+
	      "<div class=\"modal-dialog\">"+
	          "<div class=\"modal-content\">"+
	              "<div class=\"modal-header\">"+
	                     "<button type=\"button\" class=\"close\" data-dismiss=\"modal\"><span class=\"sr-only\">Close<\/span><\/button>"+
	                      "<h3 class=\"modal-title\">Code Editor<\/h3>"+
	                      "<small>Options<\/small>"+
	              "<\/div>"+
	              "<div class=\"modal-body form-horizontal\">"+
	                    "<div class=\"form-group\">"+
	                      "<label for=\"inputEmail3\" class=\"col-sm-3 control-label\">Highlight Mode<\/label>"+
	""+
	                      "<div class=\"col-sm-9\">"+
	                          "<select class=\"form-control\" id=\"bennu-code-editor-modal-mode\">"+
	""+
	                          "<\/select>"+
	                          "<p class=\"help-block\">Choose what type of code you are editing<\/p>"+
	                      "<\/div>"+
	                    "<\/div>"+
	""+
	                    "<div class=\"form-group\">"+
	                          "<label for=\"inputEmail3\" class=\"col-sm-3 control-label\">Text Size<\/label>"+
	""+
	                          "<div class=\"col-sm-9\">"+
	                              "<input type=\"text\" class=\"form-control\" id=\"bennu-code-editor-modal-size\"\/>"+
	                          "<\/div>"+
	                    "<\/div>"+
	""+
	                    "<div class=\"form-group\">"+
	                          "<label for=\"inputEmail3\" class=\"col-sm-3 control-label\">Theme<\/label>"+
	""+
	                          "<div class=\"col-sm-9\">"+
	                                "<select class=\"form-control\" id=\"bennu-code-editor-modal-themes\">"+
	""+
	                                "<\/select>"+
	                                "<p class=\"help-block\">Select the editor appearance<\/p>"+
	                          "<\/div>"+
	                    "<\/div>"+
	              "<\/div>"+
	              "<div class=\"modal-footer\">"+
	                  "<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close<\/button>"+
	                  "<button type=\"button\" class=\"btn btn-primary bennu-code-editor-modal-save\">Save<\/button>"+
	              "<\/div>"+
	          "<\/div>"+
	      "<\/div>"+
	  "<\/div>";

	Bennu.codeEditor.THEMES = {
		"ambiance":"Ambiance",
		"chaos":"Chaos",
		"chrome":"Chrome",
		"clouds":"Clouds",
		"clouds_midnight":"Clouds Midnight",
		"cobalt":"Cobalt",
		"crimson_editor":"Crimson Editor",
		"dawn":"Dawn",
		"dreamweaver":"Dreamweaver",
		"eclipse":"Eclipse",
		"github":"GitHub",
		"idle_fingers":"idleFingers",
		"katzenmilch":"Katzenmilch",
		"kr_theme":"KR Theme",
		"kuroir":"Kuroir",
		"merbivore":"Merbivore",
		"merbivore_soft":"Merbivore Soft",
		"mono_industrial":"Mono Industrial",
		"monokai":"Monokai",
		"pastel_on_dark":"Pastel on Dark",
		"solarized_dark":"Solarized Dark",
		"solarized_light":"Solarized Light",
		"terminal":"Terminal",
		"textmate":"TextMate",
		"tomorrow":"Tomorrow",
		"tomorrow_night":"Tomorrow Night",
		"tomorrow_night_blue":"Tomorrow Night Blue",
		"tomorrow_night_bright":"Tomorrow Night Bright",
		"tomorrow_night_eighties":"Tomorrow Night Eighties",
		"twilight":"Twilight",
		"vibrant_ink":"Vibrant Ink",
		"xcode":"XCode"
	}

  	Bennu.codeEditor.MODES = {
  		"abap": "ABAP",
		"actionscript": "ActionScript",
		"ada": "Ada",
		"apache_conf": "Apache Configuration",
		"applescript": "AppleScript",
		"asciidoc":"AsciiDoc",
		"assembly_x86":"Assembly x86",
		"autohotkey": "AutoHotkey",
		"batchfile":"Batch file",
		"c9search":"C9 Search",
		"c_cpp": "c++",
		"cirru": "Cirru",
		"clojure": "Clojure",
		"cobol": "COBOL",
		"coffee": "CoffeeScript",
		"coldfusion": "ColdFusion",
		"csharp": "C#",
		"css": "CSS",
		"curly": "Curly",
		"d": "D",
		"dart": "Dart",
		"diff": "Diff",
		"django": "Django",
		"dockerfile": "Dockerfile",
		"dot": "Dot",
  		"eiffel": "Eiffel",
		"elixir": "Elixir",
		"elm": "Elm",
		"erlang": "Erlang",
		"forth": "Forth",
		"ftl": "FreeMarker Template Language",
		"gcode": "GCode",
		"gherkin": "Gherkin",
		"gitignore": "Gitignore",
		"glsl": "OpenGL Shading Language",
		"golang": "Go",
		"groovy": "Groovy",
		"haml": "HAML",
		"handlebars": "Handlebars",
		"haskell": "Haskell",
  		"haxe": "Haxe",
		"html": "HTML",
		"html_ruby": "HTML Ruby",
		"ini": "Ini",
		"io": "IO",
		"jack": "Jack",
		"jade": "Jade",
		"java": "java",
		"javascript": "Javascript",
		"json":"JSON",
		"jsoniq":"JSON Query Language",
		"jsp": "Java Server Pages",
		"jsx": "JSX",
		"julia": "Julia",
		"latex": "LaTeX",
		"less": "LESS",
		"liquid": "liquid",
		"lisp": "LISP",
		"livescript": "LiveScript",
		"logiql": "LogiQL",
		"lsl": "lsl",
		"lua": "Lua",
		"luapage": "Lua Page",
		"lucene": "Lucene",
		"makefile": "Makefile",
		"markdown": "Markdown",
		"matlab": "MATLAB",
		"mel": "Mel",
		"mushcode": "MUSHCode",
		"mysql": "MySQL",
		"nix": "Nix",
		"objectivec": "Objective-C",
		"ocaml": "OCaml",
  		"pascal":"Pascal",
		"perl":"perl",
		"pgsql":"PostgresQL",
		"php": "PHP",
		"plain_text": "Plain Text",
		"powershell":"PowerShell",
		"praat":"Praat",
		"prolog": "Prolog",
		"properties": "Properties",
		"protobuf":"protobuf",
		"python": "Python",
		"r": "R",
		"rdoc": "RDoc",
		"rhtml": "RHTML",
		"ruby": "Ruby",
		"rust": "Rust",
  		"sass": "SASS",
		"scad": "SCad",
		"scala": "Scala",
		"scheme": "Scheme",
		"scss": "SCSS",
		"sh": "Shell Script",
		"smarty":"smarty",
		"snippets": "Snippets",
		"soy_template": "Soy Template",
		"space": "Space",
		"sql": "SQL",
		"stylus": "Stylus",
		"svg": "SVG",
		"tcl": "Tcl",
		"tex": "TeX",
		"text": "Text",
		"textile": "Textile",
  		"toml": "Tom's Obvious, Minimal Language",
		"twig": "Twig",
		"typescript": "TypeScript",
		"vala": "Vala",
		"vbscript":"VBScript",
		"velocity": "Velocity",
		"verilog":"Verilog",
		"vhdl":"VHSIC Hardware Description Language",
		"xml": "XML",
		"xquery": "XQuery",
		"yaml": "YAML" 
	};

  	Bennu.codeEditor.MIME_TYPES = {
  		"text/x-c":"c_cpp"
  	};

  	getModeFromMime = function(mime){
  		mime = mime.toLowerCase()
  		// the mode is explicit
  		if (mime in Bennu.codeEditor.MODES){
  			return mime
  		}

  		// mimetype is awkward and can not be parsed
  		if (mime in Bennu.codeEditor.MIME_TYPES){
  			return Bennu.codeEditor.MIME_TYPES[mime];
  		}

  		// Trying to find a correspondence between the name and the mode
  		var cls = mime.split("/")

  		if(cls.length == 2){
  			cls = cls[1];
	  		if(cls.startsWith("x-")){
	  			cls = cls.substring(2)
	  		}
	  		if (cls in Bennu.codeEditor.MODES){
	  			return cls;
	  		}	
  		}
  		// fall back
  		return "plain_text";

  	}

 	Bennu.codeEditor.require = function(callback){
		Bennu.ensure(Bennu.contextPath + "/webjars/ace/1.1.9/src-min-noconflict/ace.js", function(){
			if (callback) {
				callback();
			}
		});
  	};

  	Bennu.codeEditor.changeMode = function(mime, el){
  		var editor = el.data("editor");
  		var sbel = el.data("statusbar");
  		var mode = getModeFromMime(mime);
  		el.data("mode",mode);
  		editor.getSession().setMode("ace/mode/" + mode);

  		$(".bennu-code-editor-lang",sbel).html(Bennu.codeEditor.MODES[mode]);
  	}

  	Bennu.codeEditor.showModal = function(el){
		$("#bennu-code-editor-modal-mode").val(el.data("mode"));
		$("#bennu-code-editor-modal-size").val(localStorage.getItem("bennuCodeEditorFontSize"));
		$("#bennu-code-editor-modal-themes").val(localStorage.getItem("bennuCodeEditorTheme"));
		$("#bennu-code-editor-modal").data("editor",el);
		$("#bennu-code-editor-modal").modal("show");
  	}

  	Bennu.codeEditor.changeTheme = function(theme){
  		$("[bennu-code-editor]").map(function(i,e){
  			$(e).data("editor").setTheme("ace/theme/" + theme);	
  		});

      	localStorage.setItem("bennuCodeEditorTheme", theme);
  	}

  	Bennu.codeEditor.changeFontSize = function(size){
  		$("[bennu-code-editor]").map(function(i,e){
  			$(e).data("editor").setFontSize(parseInt(size));
  		});
  		
      	localStorage.setItem("bennuCodeEditorFontSize", size);
  	}

	Bennu.codeEditor.createWidget = function(el){

		Bennu.ensure(Bennu.contextPath + "/webjars/ace/1.1.9/src-min-noconflict/ace.js", function(){
			Bennu.ensure(Bennu.contextPath + "/webjars/ace/1.1.9/src-min-noconflict/ext-statusbar.js", function(){	
			el = $(el);

			var sbel = $('<div class="bennu-code-editor-statusbar">Mode: <a href="#" class="bennu-code-editor-lang"></a> &#8212; </div>');

			el.after(sbel);

			var editorEl = el[0];

			var editor = ace.edit(editorEl);
			var StatusBar = ace.require("ace/ext/statusbar").StatusBar;
    		
    		var statusBar = new StatusBar(editor, sbel[0]);

			el.data("editor",editor);
			el.data("statusbar",sbel);
			sbel.data("editor", el);

			var fSize = localStorage.getItem("bennuCodeEditorFontSize");
			var theme = localStorage.getItem("bennuCodeEditorTheme");

			if (!fSize){
				fSize = 13;
				localStorage.setItem("bennuCodeEditorFontSize", fSize);
			}

			if (!theme){
				theme = "clouds";
				localStorage.setItem("bennuCodeEditorTheme", theme);
			}

			editor.setTheme("ace/theme/" + theme);
			editor.setFontSize(parseInt(fSize));
		    
			if( $("#bennu-code-editor-modal").length == 0){
				$(document.body).append(modalTemplate);
				
				var modal = $("#bennu-code-editor-modal");
				var themes = $("#bennu-code-editor-modal-mode");
				for(var key in Bennu.codeEditor.MODES){
					themes.append("<option value='" + key + "'>" + Bennu.codeEditor.MODES[key] + "</options>")
				}

				var themes = $("#bennu-code-editor-modal-themes");
				for(var key in Bennu.codeEditor.THEMES){
					themes.append("<option value='" + key + "'>" + Bennu.codeEditor.THEMES[key] + "</options>")
				}

				$(".bennu-code-editor-modal-save").on("click",function(e){
					Bennu.codeEditor.changeTheme($("#bennu-code-editor-modal-themes").val());
					Bennu.codeEditor.changeFontSize($("#bennu-code-editor-modal-size").val());
					Bennu.codeEditor.changeMode($("#bennu-code-editor-modal-mode").val(), el);
					modal.modal("hide");
				});

			}


			$(".bennu-code-editor-lang",sbel).on("click",function(e){
				var el = $(e.target).closest(".bennu-code-editor-statusbar").data("editor");

				Bennu.codeEditor.showModal(el);
			});

			var mode = $(el).attr("type") || "plain_text"

			Bennu.codeEditor.changeMode(mode,el);

		    editor.setHighlightActiveLine(false);
		    editor.setShowPrintMargin(false);

		    var handler = {
	            get:function(){
	                return editor.getSession().getValue();
	            },

	            set:function(val){
	                editor.setValue(val, 1);
	                $(el).trigger("change");
	            },

	            clear:function(val){
	                editor.setValue("", 1);
	            },

	            onchange:function(fn){
	                events.push(fn);
	            },

	            trigger: function(){
	                for (var i = 0; i < events.length; i++) {
	                    events[i].apply(el,{
	                        type:"change",
	                        target:el
	                    });
	                }
	            },

	            remove: function(){
	                el.remove();
	            }
	        }
	        el.data("handler",handler);
			
	        return handler;
    		});
		});	
	};
})();
