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
        },
        
        codeMirror: undefined,
        
        onShow: function () {
        	var that = this;
        	require(['codemirror'], function(CodeMirror) {
        		require(['codemirror-clike'], function(CLike) {
        			var codeArea = $('#code')[0];
        			that.codeMirror = CodeMirror.fromTextArea(codeArea, {lineNumbers : true, mode:"text/x-java", theme:"eclipse", viewportMargin: Infinity});
        		});
			});
        },
        
        compileTask: function(e) {
        	e.preventDefault();
        	var javaCode = this.codeMirror.getValue();
        	var packageName = new RegExp("package (.*);").exec(javaCode)[1];
        	var className = new RegExp("public class (.*) extends").exec(javaCode)[1];
        	var fqn = packageName + "." + className;
        	console.log("FQN : " + fqn);
        	$.ajax( {
        		type: "POST",
        		url : "../api/bennu-scheduler/custom/compile", 
        		data: {name : fqn, code : javaCode},
        		async: false,
        		success: function(data) {
        			$("#result").html("<pre>" + data + "</pre>");
        		},
        		dataType:"text"
        	});
        },
        
        runTask: function(e) {
        	e.preventDefault();
        	var javaCode = this.codeMirror.getValue();
        	var packageName = new RegExp("package (.*);").exec(javaCode)[1];
        	var className = new RegExp("public class (.*) extends").exec(javaCode)[1];
        	var fqn = packageName + "." + className;
        	console.log("FQN : " + fqn);
        	$.ajax( {
        		type: "POST",
        		url : "../api/bennu-scheduler/custom", 
        		data: {name : fqn, code : javaCode},
        		success: function(data) {
        			Bankai.router.navigate("#custom", true);
        		},
        		dataType:"text"
        	});
        }
        
    });
});