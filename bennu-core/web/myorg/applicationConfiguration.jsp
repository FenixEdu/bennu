<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message key="label.configuration.title" bundle="MYORG_RESOURCES" />
</h2>

<ul>
	<li>
			<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=manageSystemGroups">
				<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
			</html:link>
	</li>
	<li>
		<!-- HAS_CONTEXT --><html:link page="/scheduler.do?method=viewScheduler">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.tasks.scheduleing"/>
		</html:link>
	</li>
	<li>
		<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=prepareAddVirtualHost">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.virtualHost.add"/>
		</html:link>
	</li>
</ul>

<fr:view name="myOrg" property="virtualHosts" schema="virtualHost.application.configuration.summary">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />

		<fr:property name="link(edit)" value="/configuration.do?method=basicApplicationConfiguration" />
		<fr:property name="key(edit)" value="label.edit" />
		<fr:property name="param(edit)" value="OID/virtualHostId" />
		<fr:property name="bundle(edit)" value="MYORG_RESOURCES" />
		<fr:property name="order(edit)" value="1" />

		<fr:property name="link(menu)" value="/configuration.do?method=manageMenus" />
		<fr:property name="key(menu)" value="label.configuration.manage.menus.features" />
		<fr:property name="param(menu)" value="OID/virtualHostToManageId" />
		<fr:property name="bundle(menu)" value="MYORG_RESOURCES" />
		<fr:property name="order(menu)" value="2" />

		<fr:property name="link(delete)" value="/configuration.do?method=deleteVirtualHost" />
		<fr:property name="key(delete)" value="label.configuration.vitual.host.delete" />
		<fr:property name="param(delete)" value="OID/virtualHostToManageId" />
		<fr:property name="bundle(delete)" value="MYORG_RESOURCES" />
		<fr:property name="order(delete)" value="3" />
	</fr:layout>
</fr:view>
