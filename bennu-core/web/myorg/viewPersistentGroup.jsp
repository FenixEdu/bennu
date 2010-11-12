<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

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