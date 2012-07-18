<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.name"/>:
	<bean:write name="taskLog" property="task.localizedName"/>
</h2>

<table class="tstyle2">
	<tr>
		<th>
			<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.log.taskStart"/>
		</th>
		<th>
			<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.log.taskEnd"/>
		</th>
		<th>
			<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.log.successful"/>
		</th>
	</tr>
	<tr>
		<td>
			<bean:write name="taskLog" property="taskStart"/>
		</td>
		<td>
			<bean:write name="taskLog" property="taskEnd"/>
		</td>
		<td>
			<bean:write name="taskLog" property="successful"/>
		</td>
	</tr>
</table>

<logic:iterate id="line" name="taskLog" property="outputLines">
	<p><bean:write name="line"/></p>
</logic:iterate>
