<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message key="label.configuration.title" bundle="MYORG_RESOURCES" />
</h2>

<ul>
	<li>
			<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=manageSystemGroups">
				<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
			</html:link>
	</li>
	<li>
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/scheduler.do?method=viewScheduler">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.tasks.scheduleing"/>
		</html:link>
	</li>
	<li>
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=prepareAddVirtualHost">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.virtualHost.add"/>
		</html:link>
	</li>
	<li>
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=viewSystemConfig">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.viewProperties"/>
		</html:link>
	</li>
	<li>
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/vaadinContext.do?method=forwardToVaadin#DomainBrowser">
			Domain Browser
		</html:link>
	</li>
</ul>

<fr:view name="myOrg" property="virtualHosts" schema="virtualHost.application.configuration.summary">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />

		<fr:property name="link(edit)" value="/configuration.do?method=basicApplicationConfiguration" />
		<fr:property name="key(edit)" value="label.edit" />
		<fr:property name="param(edit)" value="externalId/virtualHostId" />
		<fr:property name="bundle(edit)" value="MYORG_RESOURCES" />
		<fr:property name="order(edit)" value="1" />

		<fr:property name="link(menu)" value="/configuration.do?method=manageMenus" />
		<fr:property name="key(menu)" value="label.configuration.manage.menus.features" />
		<fr:property name="param(menu)" value="externalId/virtualHostToManageId" />
		<fr:property name="bundle(menu)" value="MYORG_RESOURCES" />
		<fr:property name="order(menu)" value="2" />

		<fr:property name="link(delete)" value="/configuration.do?method=deleteVirtualHost" />
		<fr:property name="key(delete)" value="label.configuration.vitual.host.delete" />
		<fr:property name="param(delete)" value="externalId/virtualHostToManageId" />
		<fr:property name="bundle(delete)" value="MYORG_RESOURCES" />
		<fr:property name="order(delete)" value="3" />
	</fr:layout>
</fr:view>
