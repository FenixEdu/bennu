<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.listCustomTasks"/>
</h2>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.in.execution"/>
</h3>

<logic:empty name="runningExecuters">
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.in.execution.none"/>
</logic:empty>
<logic:notEmpty name="runningExecuters">
	<fr:view name="runningExecuters" schema="myorg.domain.scheduler.ClassBean.Executer">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.terminated"/>
</h3>

<fr:view name="customTaskLogs" schema="myorg.domain.scheduler.CustomTaskLog.list">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />

		<fr:property name="link(view)" value="/scheduler.do?method=viewCustomTaskLog" />
		<fr:property name="key(view)" value="link.scheduler.customTask.view" />
		<fr:property name="param(view)" value="externalId/customTaskLogId" />
		<fr:property name="bundle(view)" value="MYORG_RESOURCES" />
		<fr:property name="order(view)" value="1" />
	</fr:layout>
</fr:view>

