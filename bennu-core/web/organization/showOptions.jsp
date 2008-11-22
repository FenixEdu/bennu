<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<h2><bean:message key="label.options" bundle="ORGANIZATION_RESOURCES" /></h2>
<ul>
	<li><html:link action="/organization.do?method=viewPartyTypes"><bean:message key="label.party.type" bundle="ORGANIZATION_RESOURCES"/></html:link></li>
	<li><html:link action="/organization.do?method=viewAccountabilityTypes"><bean:message key="label.accountability.type" bundle="ORGANIZATION_RESOURCES"/></html:link></li>
</ul>
