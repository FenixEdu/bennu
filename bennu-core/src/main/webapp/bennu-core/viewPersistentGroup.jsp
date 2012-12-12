<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message key="label.configuration.manage.group" bundle="MYORG_RESOURCES"/>: <bean:write name="persistentGroup" property="name"/>
</h2>

<logic:present name="persistentGroup" property="members">
	<logic:iterate id="user" name="persistentGroup" property="members">
		<bean:write name="user" property="username"/>
		<bean:define id="url">/configuration.do?method=removeUser&persistentGroupId=<bean:write name="persistentGroup" property="externalId"/></bean:define>
		<html:link action="<%= url %>" paramId="userId" paramName="user" paramProperty="externalId">
			<bean:message key="label.remove" bundle="MYORG_RESOURCES"/>
		</html:link>
		<br/>
	</logic:iterate>
</logic:present>
