<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.users"/>
</h2>

<fr:edit id="searchUsers"
		name="searchUsers"
		type="myorg.domain.SearchUsers"
		schema="application.searchUsers"
		action="manageUsers.do?method=searchUsers">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>

<logic:present role="myorg.domain.RoleType.MANAGER,myorg.domain.RoleType.USER_MANAGER">
	<br/>
	<html:link action="manageUsers.do?method=prepareCreateNewUser">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.users.create.new"/>
	</html:link>
</logic:present>

<logic:present name="searchUsers" property="user">
	<br/>
	<br/>
	<h3>
		<bean:message bundle="MYORG_RESOURCES" key="label.user"/>
		:
		<bean:write name="searchUsers" property="user.username"/>
	</h3>
	<logic:present role="myorg.domain.RoleType.MANAGER,myorg.domain.RoleType.USER_MANAGER">
		<p>
			<html:link action="manageUsers.do?method=generatePassword" paramId="userId" paramName="searchUsers" paramProperty="user.OID">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.generate.password"/>
			</html:link>
			<logic:present name="password">
				<br/>
				<br/>
				<font size="4" color="red">
					<bean:write name="password"/>
				</font>
			</logic:present>
		</p>
	</logic:present>
	<h4>
		<bean:message bundle="MYORG_RESOURCES" key="label.user.lastLogoutDateTime"/>
	</h4>
	<p>
		<bean:write name="searchUsers" property="user.lastLogoutDateTime"/>
	</p>
	<h4>
		<bean:message bundle="MYORG_RESOURCES" key="label.user.groups"/>
	</h4>
	<p>
		<logic:present role="myorg.domain.RoleType.MANAGER,myorg.domain.RoleType.USER_MANAGER">
			<html:link action="manageUsers.do?method=prepareAddGroup" paramId="userId" paramName="searchUsers" paramProperty="user.OID">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.group.add"/>
			</html:link>
			<br/>
		</logic:present>
		<logic:iterate id="peopleGroup" name="searchUsers" property="user.peopleGroups">
			<bean:write name="peopleGroup" property="name"/>
			<bean:define id="url">manageUsers.do?method=removeGroup&amp;peopleId=<bean:write name="peopleGroup" property="OID"/></bean:define>
			<html:link action="<%= url %>" paramId="userId" paramName="searchUsers" paramProperty="user.OID">
				<bean:message bundle="MYORG_RESOURCES" key="label.application.users.group.remove"/>
			</html:link>			
			<br/>
		</logic:iterate>
	</p>
</logic:present>