<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

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
<div style="border-style: dotted; border-width: thin; padding: 10px;">
	<ul>
		<logic:iterate id="file" name="customTaskLog" property="genericFileSet">
			<li>
				<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=downloadOutputFile" paramId="fileId" paramName="file" paramProperty="externalId">
					<bean:write name="file" property="displayName"/>
				</html:link>
			</li>
		</logic:iterate>
	</ul>
</div>
