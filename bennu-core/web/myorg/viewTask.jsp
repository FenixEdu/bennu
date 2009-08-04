<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.name"/>:
	<bean:write name="task" property="localizedName"/>
</h2>

<bean:define id="taskId" name="task" property="externalId"/>

<logic:notEmpty name="task" property="taskConfigurations">
	<table class="tstyle2">
		<tr>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.minute"/>
			</th>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.hour"/>
			</th>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.day"/>
			</th>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.month"/>
			</th>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.dayofweek"/>
			</th>
			<th>
			</th>
		</tr>
		<logic:iterate id="taskConfiguration" name="task" property="taskConfigurations">
			<tr>
				<td>
					<bean:write name="taskConfiguration" property="minute"/>
				</td>
				<td>
					<bean:write name="taskConfiguration" property="hour"/>
				</td>
				<td>
					<bean:write name="taskConfiguration" property="day"/>
				</td>
				<td>
					<bean:write name="taskConfiguration" property="month"/>
				</td>
				<td>
					<bean:write name="taskConfiguration" property="dayofweek"/>
				</td>
				<td>
					<!-- HAS_CONTEXT --><html:link page="<%= "/scheduler.do?method=deleteTaskConfiguration&taskId=" + taskId.toString() %>" paramId="taskConfigurationId" paramName="taskConfiguration" paramProperty="externalId">
						<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.delete"/>
					</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>

<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=prepareAddTaskConfiguration" paramId="taskId" paramName="task" paramProperty="externalId">
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.add.configuration"/>
</html:link>

<logic:notEmpty name="task" property="taskLogs">
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
			<th>
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
				<td>
					<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=viewTaskLog" paramId="taskLogId" paramName="taskLog" paramProperty="externalId">
						<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.log.view"/>
					</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>
