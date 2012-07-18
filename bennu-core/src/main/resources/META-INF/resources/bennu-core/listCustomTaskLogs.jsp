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
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.in.execution"/>
</h3>

<logic:empty name="runningExecuters">
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.in.execution.none"/>
</logic:empty>
<logic:notEmpty name="runningExecuters">
	<fr:view name="runningExecuters" schema="pt.ist.bennu.core.domain.scheduler.ClassBean.Executer">
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="link.scheduler.customTasks.terminated"/>
</h3>

<fr:view name="customTaskLogAggregates">
	<fr:schema type="pt.ist.bennu.core.presentationTier.CustomTaskLogAggregate" bundle="MYORG_RESOURCES">
		<fr:slot name="className" key="label.scheduler.className"/>
		<fr:slot name="lastUploadDate" key="label.scheduler.customTaskAggregate.lastUploadDate"/>
		<fr:slot name="size" key="label.scheduler.customTaskAggregate.size"/>
	</fr:schema>
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />

		<fr:property name="link(view)" value="/scheduler.do?method=searchCustomTaskLogs" />
		<fr:property name="key(view)" value="label.scheduler.customTaskAggregate.view" />
		<fr:property name="param(view)" value="className/className" />
		<fr:property name="bundle(view)" value="MYORG_RESOURCES" />
		<fr:property name="order(view)" value="1" />

		<fr:property name="link(delete)" value="/scheduler.do?method=deleteCustomTaskLogs" />
		<fr:property name="key(delete)" value="label.scheduler.customTaskAggregate.delete" />
		<fr:property name="param(delete)" value="className/className" />
		<fr:property name="confirmationBundle(delete)" value="MYORG_RESOURCES" />
		<fr:property name="confirmationKey(delete)" value="label.scheduler.customTaskAggregate.delete.confirmation" />
		<fr:property name="confirmationTitleKey(delete)" value="label.scheduler.customTaskAggregate.delete" />
		<fr:property name="bundle(delete)" value="MYORG_RESOURCES" />
		<fr:property name="order(delete)" value="2" />
	</fr:layout>
</fr:view>

