<%@page import="pt.ist.bennu.core._development.PropertiesManager"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.listCustomTasks"/>
</h2>

<table class="tstyle2">
	<tr>
		<th><bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.className"/></th>
		<th><bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.uploaded"/></th>
		<th><bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.taskStart"/></th>
		<th><bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.taskEnd"/></th>
	</tr>
	<tr>
		<td><bean:write name="customTaskLog" property="className"/></td>
		<td><bean:write name="customTaskLog" property="uploaded"/></td>
		<td><bean:write name="customTaskLog" property="taskStart"/></td>
		<td><bean:write name="customTaskLog" property="taskEnd"/></td>
	</tr>
</table>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.contents"/>
</h3>
<div style="border-style: dotted; border-width: thin; padding: 10px;">
	<pre><bean:write name="customTaskLog" property="contents"/></pre>
</div>
<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.output"/>
</h3>
<div style="border-style: dotted; border-width: thin; padding: 10px;">
	<pre><bean:write name="customTaskLog" property="output"/></pre>
</div>
<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.customTask.files"/>
</h3>

<%
String disableBuggyFileList = PropertiesManager.getProperty("scheduler.disable.buggy.fileList");
if ( disableBuggyFileList == null || disableBuggyFileList.equalsIgnoreCase("false") && !disableBuggyFileList.equalsIgnoreCase("true")) { %>
<div style="border-style: dotted; border-width: thin; padding: 10px;">
	<ul>
		<logic:iterate id="file" name="customTaskLog" property="genericFileSet">
			<li>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/scheduler.do?method=downloadOutputFile" paramId="fileId" paramName="file" paramProperty="externalId">
					<bean:write name="file" property="displayName"/>
				</html:link>
			</li>
		</logic:iterate>
	</ul>
</div>
<% } %>
