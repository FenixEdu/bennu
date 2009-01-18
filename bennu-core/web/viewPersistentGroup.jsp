<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.configuration.manage.group" bundle="MYORG_RESOURCES"/>: <bean:write name="persistentGroup" property="name"/></h2>

<logic:iterate id="user" name="persistentGroup" property="users">
	<bean:write name="user" property="username"/>
	<br/>
</logic:iterate>