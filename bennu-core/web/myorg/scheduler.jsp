<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<%@page import="myorg.domain.scheduler.Task"%>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.tasks.scheduleing"/>
</h2>

<ul>
	<!-- HAS_CONTEXT --><li><html:link page="/configuration.do?method=applicationConfiguration">
		<bean:message bundle="MYORG_RESOURCES" key="link.back"/>
	</html:link></li>
</ul>

	<p>
		<html:link page="/scheduler.do?method=prepareLoadAndRun">
			<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.load.and.run"/>
		</html:link>
	</p>
	<p>
		<html:link page="/scheduler.do?method=listCustomTaskLogs">
			<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.listCustomTasks"/>
		</html:link>
	</p>
	
<h3>
	<bean:message bundle="MYORG_RESOURCES" key="tasks.in.execution"/>
</h3>
<logic:empty name="executingTasks">
	<p>
		<bean:message bundle="MYORG_RESOURCES" key="message.no.tasks.in.execution.now"/>
	</p>
</logic:empty>
<logic:notEmpty name="executingTasks">
	<table class="tstyle2">
		<tr>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.name"/>
			</th>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="repeat.on.fail"/>
			</th>
			<th></th>
		</tr>
		<logic:iterate id="task" name="executingTasks">
			<tr>
				<td>
					<html:link page="/scheduler.do?method=viewTask" paramId="taskId" paramName="task" paramProperty="externalId">
						<bean:write name="task" property="localizedName"/>
					</html:link>
				</td>
				<td>
					<bean:write name="task" property="repeatedOnFailure"/>
				</td>
				<td>
					<html:link page="/scheduler.do?method=stopRunning" paramId="taskId" paramName="task" paramProperty="externalId">
						<bean:message bundle="MYORG_RESOURCES" key="stop.running"/>
					</html:link>
				</td>
			</tr>
		</logic:iterate>
	</table>
</logic:notEmpty>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.tasks.active"/>
</h3>
<logic:empty name="activeTasks">
	<p>
		<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.tasks.none.active"/>
	</p>
</logic:empty>
<logic:notEmpty name="activeTasks">
	<table class="tstyle2">
		<tr>
			<th>
				<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.name"/>
			</th>
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
		</tr>
		<logic:iterate id="task" name="activeTasks">
			<bean:size id="numberConfigurations" name="task" property="taskConfigurations"/>
			<logic:iterate id="taskConfiguration" name="task" property="taskConfigurations" length="1">
				<tr>
					<td rowspan="<%= numberConfigurations %>">
						<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=viewTask" paramId="taskId" paramName="task" paramProperty="externalId">
							<bean:write name="task" property="localizedName"/>
						</html:link>
					</td>
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
				</tr>
			</logic:iterate>
			<logic:iterate id="taskConfiguration" name="task" property="taskConfigurations" offset="1">
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
				</tr>
			</logic:iterate>
		</logic:iterate>
	</table>
</logic:notEmpty>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.tasks.inactive"/>
</h3>
<logic:empty name="inactiveTasks">
	<p>
		<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.tasks.none.inactive"/>
	</p>
</logic:empty>
<logic:notEmpty name="inactiveTasks">
	<logic:iterate id="task" name="inactiveTasks">
		<p>
			<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=viewTask" paramId="taskId" paramName="task" paramProperty="externalId">
				<bean:write name="task" property="localizedName"/>
			</html:link>
		</p>
	</logic:iterate>
</logic:notEmpty>