define([
    'jquery',
    'backbone',
    'marionette',
    'moment',
    'app',
    'text!templates/AddCustomTask.html'
], function($, Backbone, Marionette, moment, App, tpl) {

    return Backbone.Marionette.ItemView.extend({

        template: tpl,
        
        modelEvents: {
        	"change" : "render"
        },
        
        events: {
        	"click .btn-run" : "runTask",
        	"click .btn-compile": "compileTask",
        	"click .btn-upload" : "triggerUploadJavaCodeFile",
        	"change #javaCodeFile": "uploadJavaCodeFile",
        },
        
        codeMirror: undefined,
        
        triggerUploadJavaCodeFile: function(e) {
        	e.preventDefault();
        	$("#javaCodeFile").click();
        },
        
        uploadJavaCodeFile: function(e) {
        	e.preventDefault();
        	var files = e.target.files; // FileList object
        	var that = this;
        	for ( var i = 0; i < files.length; i++) {
				var file = files[i];
				if (!file.type.match("text.*")) {
					continue;
				}

				var reader = new FileReader();
				reader.onload = (function(f) {
					return function(e) {
						that.codeMirror.setValue(e.target.result);
					};
				})(file);

				reader.readAsText(file);
			}
        },
        
        onShow: function () {
        	var that = this;
        	require(['libs/codemirror/clike'], function(CodeMirror) {
        		require([], function() {
        			var codeArea = $('#code')[0];
        			that.codeMirror = CodeMirror.fromTextArea(codeArea, {lineNumbers : true, mode:"text/x-java", theme:"eclipse", viewportMargin: Infinity});
        			that.codeMirror.on("change", function() {
        				$(".btn-compile").attr('disabled', false);
        				$(".btn-run").attr('disabled', true);
        			});
        		});
			});
        	$(".btn-run").attr('disabled', true);
        	$(".btn-compile").attr('disabled', false);
        },
        
        compileTask: function(e) {
        	e.preventDefault();
        	var javaCode = this.codeMirror.getValue();
        	var packageName = new RegExp("package (.*);").exec(javaCode)[1];
        	var className = new RegExp("public class (.*) extends").exec(javaCode)[1];
        	var fqn = packageName + "." + className;
        	$.ajax( {
        		type: "POST",
        		url : "../api/bennu-scheduler/custom/compile", 
        		data: {name : fqn, code : javaCode},
        		async: false,
        		success: function(result) {
        			var output;
        			if (result.compileOK) {
        				output = "The compilation was successful!";
        				$(".btn-run").attr('disabled', false);
        				$(".btn-compile").attr('disabled', true);
        			} else {
        				output = result.error;
        			}
        			$("#result").html(output);
        		},
        		dataType:"json"
        	});
        },
        
        runTask: function(e) {
        	e.preventDefault();
        	var javaCode = this.codeMirror.getValue();
        	var packageName = new RegExp("package (.*);").exec(javaCode)[1];
        	var className = new RegExp("public class (.*) extends").exec(javaCode)[1];
        	var fqn = packageName + "." + className;
        	$.ajax( {
        		type: "POST",
        		url : "../api/bennu-scheduler/custom", 
        		data: {name : fqn, code : javaCode},
        		success: function(data) {
        			App.Router.navigate("custom", true);
        		},
        		dataType:"text"
        	});
        }
        
    });
});