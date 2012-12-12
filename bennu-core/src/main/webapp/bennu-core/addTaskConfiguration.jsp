<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.task.configure"/>:
	<bean:write name="taskConfigurationBean" property="task.localizedName"/>
</h2>

<bean:define id="taskId" name="taskConfigurationBean" property="task.externalId" type="java.lang.String"/>
<fr:edit id="taskConfigurationBean" name="taskConfigurationBean" schema="pt.ist.bennu.core.domain.scheduler.TaskConfigurationBean"
		action="scheduler.do?method=addTaskConfiguration">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
	<fr:destination name="cancel" path="<%="/scheduler.do?method=viewTask&taskId="+taskId%>" />
</fr:edit>
