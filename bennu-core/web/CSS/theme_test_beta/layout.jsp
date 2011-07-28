
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>

<!--  HEAD.JSP -->
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@page import="myorg.domain.VirtualHost"%>

<!--  PAGE_HEADER.JSP -->
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.utl.ist.fenix.tools.util.i18n.Language"%>
<%@page import="pt.ist.fenixWebFramework.security.User"%>


<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">

<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
%>


<head>
	<logic:iterate id="head" collection="<%= layoutContext.getHead() %>" type="java.lang.String">
		<jsp:include page="<%= head %>"/>
	</logic:iterate>
	
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
	<script type="text/javascript" src="http://localhost:8080/bennu/CSS/theme_test_beta/js/script.js"></script>
	
</head>




<body class="top">


<div id="header" class="container_12">
	<img src="http://localhost:8080/bennu/CSS/theme_test_beta/images/jobbank.png" alt="IST Job Bank" />
	
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
	<logic:notPresent name="USER_SESSION_ATTRIBUTE">
		<% if (isCasEnabled) {%>
			<div class="login">
				<% final String portString = request.getServerPort() == 80 || request.getServerPort() == 443 ? "" : ":" + request.getServerPort(); %>
				<bean:define id="loginUrl"><%= FenixWebFramework.getConfig().getCasConfig(serverName).getCasLoginUrl() + "https" + "://" + request.getServerName() + contextPath %>/</bean:define>
				<html:link href="<%= loginUrl %>"><bean:message key="label.login.link" bundle="MYORG_RESOURCES"/></html:link>
			</div>
		<% } else { %>
		
		
			<div id="login">
				<a href="#" class="ainda-nao">Ainda não tem conta?</a>
				<div id="botao" class="posicao-azul">
					<a href="javascript:void(0);" class="botao azul" id="botao-login"><strong>Login</strong></a>
				</div><!-- botão -->
			</div><!-- login -->
			
			<div id="login-escolha">
				Fazer login como 
				<a href="javascript:void(0);" class="login-link" id="login-link-empresa">Empresa</a> ou 
				<a href="javascript:void(0);" class="login-link" id="login-link-aluno">Aluno</a>
			</div><!-- login-escolha -->
			
			<div id="login-form">
			
				<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
					<input type="hidden" name="method" value="login"/>
					<div class="login-block">
						<label class="login-username"><bean:message key="label.login.username" bundle="MYORG_RESOURCES"/></label><br />
						<input type="text" name="username" size="20" />
					</div>
					<div class="login-block">
						<label class="login-password"><bean:message key="label.login.password" bundle="MYORG_RESOURCES"/></label><br />
						<input type="password" name="password" size="20" />
					</div>
					<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
					<input class="login-submit" type="submit" name="Submit" value="<%= loginLabel %>"/>
				</form>
				
			</div>
		
			<%--
			<form action="<%= contextPath %>/authenticationAction.do" class="login" method="post">
				<input type="hidden" name="method" value="login"/>
				<span><bean:message key="label.login.username" bundle="MYORG_RESOURCES"/>: <input type="text" name="username" size="10"/></span>
				<span><bean:message key="label.login.password" bundle="MYORG_RESOURCES"/>: <input type="password" name="password" size="10"/></span>
				<bean:define id="loginLabel"><bean:message key="label.login.submit" bundle="MYORG_RESOURCES"/></bean:define>
				<input class="inputbuttonlogin" type="submit" name="Submit" value="<%= loginLabel %>"/>
			</form>
			--%>
			
			
		<% } %>
	</logic:notPresent>
	<% } %>



	
	<logic:present name="USER_SESSION_ATTRIBUTE">
		<div id="hello-user">
			<p>
				<%
					final User user = (User) request.getSession(false).getAttribute("USER_SESSION_ATTRIBUTE");
					if (user.hasRole("myorg.domain.RoleType.MANAGER")) {
				%>
						<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=applicationConfiguration">
							<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>
						</html:link> |
				<%	} %>
				<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/> |
				<logic:present name="virtualHost" property="helpLink">
					<logic:notEmpty name="virtualHost" property="helpLink">
						<bean:define id="helpUrl"><bean:write name="virtualHost" property="helpLink"/></bean:define>
						<a href="<%= helpUrl %>" target="_blank"><bean:message key="label.help.link" bundle="MYORG_RESOURCES"/></a> |  
					</logic:notEmpty>
				</logic:present>
				<html:link action="/authenticationAction.do?method=logout"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
			</p>
		</div>
	</logic:present>
	

	

</div><!-- header -->


<%--
<jsp:include page="<%= layoutContext.getPageHeader() %>"/>

<jsp:include page="<%= layoutContext.getMenuTop() %>"/>

<jsp:include page="<%= layoutContext.getSubMenuTop() %>"/>


<jsp:include page="<%= layoutContext.getPageOperations() %>"/>
<logic:equal name="virtualHost" property="breadCrumbsEnabled" value="true">
	<jsp:include page="<%= layoutContext.getBreadCrumbs() %>"/>
</logic:equal>
<jsp:include page="<%= layoutContext.getBody() %>"/>


<jsp:include page="<%= layoutContext.getFooter() %>"/>
--%>









<div id="wrap" class="container_12">
	
	<div class="topmenu">
		
		<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
		
		<jsp:include page="<%= layoutContext.getSubMenuTop() %>"/>
		
		<!--
		<ul>
			<li><a href="#">Ofertas</a></li>
			<li><a href="#">Alunos</a></li>
			<li><a href="#">Pesquisa</a></li>
		</ul>
		-->
	</div>
	
	<div id="conteudo">
		<jsp:include page="<%= layoutContext.getPageOperations() %>"/>
		<logic:equal name="virtualHost" property="breadCrumbsEnabled" value="true">
			<jsp:include page="<%= layoutContext.getBreadCrumbs() %>"/>
		</logic:equal>
		<jsp:include page="<%= layoutContext.getBody() %>"/>
	</div>
		
		
		<div id="footer">
			<p class="copyright">&copy;<dt:format pattern="yyyy"><dt:currentTime/></dt:format> <bean:write name="virtualHost" property="applicationCopyright"/></p>
		</div>
	
	
</div><!-- wrap -->


</body>
</html:html>