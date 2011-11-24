<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>

<%
	final String contextPath = request.getContextPath();
	final String serverName = request.getServerName();
	final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
	// If no config is found don't allow login.
	if (casConfig == null) {
%>
		<!-- context: <%= contextPath %> - serverName: <%= serverName %> -->
<%
	} else {
		final boolean isCasEnabled = casConfig.isCasEnabled();
%>
<div id="login" class="login">
	<logic:notPresent name="USER_SESSION_ATTRIBUTE">
		<% if (isCasEnabled) {%>
			<% final String portString = (request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort(); %>
			<bean:define id="loginUrl"><%= FenixWebFramework.getConfig().getCasConfig(serverName).getCasLoginUrl() + "https" + "://" + request.getServerName() + contextPath %>/</bean:define>
			<html:link href="<%= loginUrl %>"><bean:message key="label.login.link" bundle="MYORG_RESOURCES"/></html:link>
		<% } else { %>
			<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
				<input type="hidden" name="method" value="login"/>
				<span><bean:message key="label.login.username" bundle="MYORG_RESOURCES"/>: <input type="text" name="username" size="10"/></span>
				<span><bean:message key="label.login.password" bundle="MYORG_RESOURCES"/>: <input type="password" name="password" size="10"/></span>
				<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
				<input class="inputbuttonlogin" type="submit" name="Submit" value="<%= loginLabel %>"/>
			</form>
		<% } %>
	</logic:notPresent>
	<% } %>
	
	<logic:present name="USER_SESSION_ATTRIBUTE">
		<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/> |
		<html:link action="/authenticationAction.do?method=logout"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
	</logic:present>
</div>