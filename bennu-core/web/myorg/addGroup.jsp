<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>


<%@page import="myorg.domain.RoleType"%><h2>
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
