<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.listCustomTasks"/>
</h2>

<p>
	<html:link page="/scheduler.do?method=prepareLoadAndRun">
		<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.load.and.run"/>
	</html:link>
</p>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.terminated"/>
</h3>

<p>
	<fr:view name="customTaskLogs" schema="pt.ist.bennu.core.domain.scheduler.CustomTaskLog.list">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2" />
	
			<fr:property name="link(view)" value="/scheduler.do?method=viewCustomTaskLog" />
			<fr:property name="key(view)" value="link.scheduler.customTask.view" />
			<fr:property name="param(view)" value="externalId/customTaskLogId" />
			<fr:property name="bundle(view)" value="MYORG_RESOURCES" />
			<fr:property name="order(view)" value="1" />
	
			<fr:property name="link(reload)" value="/scheduler.do?method=reloadCustomTask" />
			<fr:property name="key(reload)" value="link.scheduler.customTask.reload" />
			<fr:property name="param(reload)" value="externalId/customTaskLogId" />
			<fr:property name="bundle(reload)" value="MYORG_RESOURCES" />
			<fr:property name="order(reload)" value="2" />
		</fr:layout>
	</fr:view>
</p>
