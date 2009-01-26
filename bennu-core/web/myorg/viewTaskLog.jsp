<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

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
	<logic:iterate id="taskLog" name="task" property="taskLogs">
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
	</logic:iterate>
</table>

<bean:write name="taskLog" property="output"/>
