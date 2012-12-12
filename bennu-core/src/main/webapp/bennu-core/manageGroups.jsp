<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.manage.system.groups"/>
</h2>

<logic:notEmpty name="persistentGroups">
	<logic:iterate id="persistentGroup" name="persistentGroups">
		<bean:write name="persistentGroup" property="name"/>
		<html:link page="/configuration.do?method=viewPersistentGroup" paramId="persistentGroupId" paramName="persistentGroup" paramProperty="externalId">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.view.group.link"/>
		</html:link>
		<br/>
	</logic:iterate>
</logic:notEmpty>
<logic:empty name="persistentGroups">
	<bean:message key="label.persistent.group.none.found" bundle="MYORG_RESOURCES"/>
</logic:empty>
