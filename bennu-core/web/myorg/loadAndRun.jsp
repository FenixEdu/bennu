<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<script>
function doit(javaCode) {
	packageName = new RegExp("package (.*);").exec(javaCode)[1];
	className = new RegExp("public class (.*) extends").exec(javaCode)[1];
	return packageName + "." + className;
}

function getTextAreaSelection(textarea) {
var start = textarea.selectionStart, end = textarea.selectionEnd;
return {
    start: start,
    end: end,
    length: end - start,
    text: textarea.value.slice(start, end)
};
}

function detectPaste(textarea, callback) {
textarea.onpaste = function() {
    var sel = getTextAreaSelection(textarea);
    var initialLength = textarea.value.length;
    window.setTimeout(function() {
        var val = textarea.value;
        var pastedTextLength = val.length - (initialLength - sel.length);
        var end = sel.start + pastedTextLength;
        callback({
            start: sel.start,
            end: end,
            length: pastedTextLength,
            text: val.slice(sel.start, end)
        });
    }, 1);
};
}
</script>


<h2>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.load.and.run"/>
</h2>
<fr:edit name="classBean" schema="myorg.domain.scheduler.ClassBean"
		action="scheduler.do?method=loadAndRun">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
	<fr:destination name="cancel" path="/scheduler.do?method=listCustomTaskLogs"/>
</fr:edit>
<script>
	
	textareas = document.getElementsByTagName('textarea');
	txtClassName = textareas[0];
	txtCode = textareas[1];
	
	function setClassname(className) {
		txtClassName.value = className;
	}
	
	detectPaste(txtCode, function(pasteInfo) {
		setClassname(doit(pasteInfo.text));
	});
	
	txtCode.onkeyup = function () { setClassname(doit(txtCode.value)); }
	
</script>