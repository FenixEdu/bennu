<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>


<%@page import="pt.ist.bennu.core.domain.RoleType"%><h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.users.group.add"/>
</h2>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.user"/>
	:
	<bean:write name="user" property="username"/>
</h3>

<p>
	<logic:iterate id="roleType" name="roleTypes">
		<bean:define id="url">manageUsers.do?method=addGroup&amp;roleType=<bean:write name="roleType" property="name"/></bean:define>
		<html:link action="<%= url %>" paramId="userId" paramName="user" paramProperty="externalId">
			<bean:write name="roleType" property="presentationName"/>
		</html:link>
		<br/>
	</logic:iterate>
</p>
