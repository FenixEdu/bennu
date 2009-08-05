<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configure"/>:
	<bean:write name="taskConfigurationBean" property="task.localizedName"/>
</h2>

<bean:define id="taskId" name="taskConfigurationBean" property="task.externalId"/>
<fr:edit id="taskConfigurationBean" name="taskConfigurationBean" schema="myorg.domain.scheduler.TaskConfigurationBean"
		action="scheduler.do?method=addTaskConfiguration">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
	<fr:destination name="cancel" path="<%="/scheduler.do?method=viewTask&taskId="+taskId%>" />
</fr:edit>
