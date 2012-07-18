<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.name"/>:
	<bean:write name="task" property="localizedName"/>
</h2>

<ul>
<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><li><html:link page="/scheduler.do?method=viewScheduler">
	<bean:message bundle="MYORG_RESOURCES" key="link.back"/>
</html:link></li>
</ul>

<bean:define id="taskId" name="task" property="externalId" type="java.lang.String"/>


<logic:equal name="task" property="executionPending" value="true">
	<p><bean:message bundle="MYORG_RESOURCES" key="in.execution"/></p>
</logic:equal>
	
<logic:equal name="task" property="repeatedOnFailure" value="true">
	<p><strong><bean:message bundle="MYORG_RESOURCES" key="repeat.on.fail"/></strong></p>
</logic:equal>
	
<logic:equal name="task" property="executionPending" value="true">
	<p><html:link page="<%= "/scheduler.do?method=stopRunning&taskId=" + taskId %>">
		<bean:message bundle="MYORG_RESOURCES" key="stop.running"/>
	</html:link></p>
</logic:equal>
<logic:equal name="task" property="executionPending" value="false">
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/scheduler.do?method=runNow" paramId="taskId" paramName="task" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.runNow"/>
	</html:link>
</logic:equal>

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
					<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= "/scheduler.do?method=deleteTaskConfiguration&taskId=" + taskId %>" paramId="taskConfigurationId" paramName="taskConfiguration" paramProperty="externalId">
						<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configuration.delete"/>
					</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>

<ul>
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><li><html:link page="/scheduler.do?method=prepareAddTaskConfiguration" paramId="taskId" paramName="task" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.add.configuration"/>
	</html:link></li>
</ul>
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
					<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/scheduler.do?method=viewTaskLog" paramId="taskLogId" paramName="taskLog" paramProperty="externalId">
						<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.log.view"/>
					</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>
