<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.users.change.password"/>
</h2>

<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.user"/>
	:
	<bean:write name="user" property="username"/>
</h3>

<bean:define id="url">manageUsers.do?method=forwardToSearchUser&amp;userId=<bean:write name="user" property="externalId"/></bean:define>
<fr:edit id="user"
		name="user"
		type="pt.ist.bennu.core.domain.User"
		schema="change.password"
		action="<%= url %>">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>
