<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>
<%@page import="myorg.presentationTier.LayoutContext"%>
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
<logic:present name="authentication.failed">
	<span class="failedLoginMessage">
		<bean:message key="authentication.failed" bundle="MYORG_RESOURCES"/>
	</span>
</logic:present>
<logic:notPresent name="USER_SESSION_ATTRIBUTE">
	<div id="login-botao">
		<div id="botao" class="posicao-azul">
			<a href="javascript:void(0);" class="botao azul" id="botao-login">
				<strong>Login</strong>
			</a>
		</div>
	</div>
	<div id="login-escolha">
		<bean:message key="label.login.as" bundle="MYORG_RESOURCES"/>
		<a href="javascript:void(0);" class="login-link" id="login-link-empresa">
			<bean:message key="label.login.company" bundle="MYORG_RESOURCES"/>
		</a>
		<bean:message key="label.or" bundle="MYORG_RESOURCES"/>
		<% if (isCasEnabled) {%>
				<% final String portString = (request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort(); %>
				<bean:define id="loginUrl"><%= FenixWebFramework.getConfig().getCasConfig(serverName).getCasLoginUrl() + "https" + "://" + request.getServerName() + contextPath %>/</bean:define>
				<html:link href="<%= loginUrl %>">
					<bean:message key="label.login.student" bundle="MYORG_RESOURCES"/>
				</html:link>
		<% } else { %>
				<a href="javascript:void(0);" class="login-link" id="login-link-aluno">
					<bean:message key="label.login.student" bundle="MYORG_RESOURCES"/>
				</a>
		<% } %>
	</div>
	<div id="login-form">
		<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
			<input type="hidden" name="method" value="login"/>
			<span><bean:message key="label.login.username" bundle="MYORG_RESOURCES"/>: <input type="text" name="username" size="10"/></span>
			<span><bean:message key="label.login.password" bundle="MYORG_RESOURCES"/>: <input type="password" name="password" size="10"/></span>
			<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
			<input class="inputbuttonlogin" type="submit" name="Submit" value="<%= loginLabel %>"/>
		</form>
		<%--
		<a href="<%= contextPath %>/enterprise.do?method=prepareToPasswordRecover" class="esqueceu-password"><bean:message bundle="JOB_BANK_RESOURCES" key="label.jobBank.login.forgotPassword"/></a>
		 --%>
	</div>
</logic:notPresent>
<% } %>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<div id="hello-user">
		<%
			final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
		%>
		<p>
			<jsp:include page="<%= layoutContext.getConfigurationLink() %>"/>
		</p>
		<jsp:include page="<%= layoutContext.getHelpLink() %>"/>
		<div id="login" class="login">
			<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/> |
			<html:link action="/authenticationAction.do?method=logout"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
		</div>
	</div>
</logic:present>
