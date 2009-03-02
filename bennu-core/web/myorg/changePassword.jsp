<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.users.change.password"/>
</h2>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.user"/>
	:
	<bean:write name="user" property="username"/>
</h3>

<bean:define id="url">manageUsers.do?method=forwardToSearchUser&amp;userId=<bean:write name="user" property="OID"/></bean:define>
<fr:edit id="user"
		name="user"
		type="myorg.domain.User"
		schema="change.password"
		action="<%= url %>">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>
