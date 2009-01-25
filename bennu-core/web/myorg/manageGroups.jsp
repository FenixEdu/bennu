<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
</h2>

<logic:notEmpty name="persistentGroups">
	<logic:iterate id="persistentGroup" name="persistentGroups">
		<bean:write name="persistentGroup" property="name"/>
		<html:link page="/configuration.do?method=viewPersistentGroup" paramId="persistentGroupId" paramName="persistentGroup" paramProperty="OID">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.view.group.link"/>
		</html:link>
		<br/>
	</logic:iterate>
</logic:notEmpty>
<logic:empty name="persistentGroups">
	<bean:message key="label.persistent.group.none.found" bundle="MYORG_RESOURCES"/>
</logic:empty>
