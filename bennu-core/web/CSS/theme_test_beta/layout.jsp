<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>

<!--  PAGE_HEADER.JSP -->
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>
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
	
	<% final String contextPath = request.getContextPath(); %>
	
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
	<script type="text/javascript" src="<%= contextPath %>/CSS/theme_test_beta/js/script.js"></script>
	
</head>


<body class="top">


	<div id="header" class="container_12">

		<a href="<%= contextPath %>" title="Job Bank">
			<img src="<%= contextPath %>/CSS/theme_test_beta/images/jobbank.png" alt="IST Job Bank" />
		</a>
	
	<%
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
				<html:link href="<%= loginUrl %>">
					<bean:message key="label.login.link" bundle="MYORG_RESOURCES" />
				</html:link>
			</div>
			<% } else { %>


			<div id="login">
				<div class="login-links-adicionais">
					<a href="#" class="ainda-nao">Ainda não tem conta?</a><br />
					<a href="#" class="esqueceu-password">Esqueceu a sua password?</a>
				</div><!-- login-adicional -->
				
				
				<div id="botao" class="posicao-azul">
					<a href="javascript:void(0);" class="botao azul" id="botao-login"><strong>Login</strong>
					</a>
				</div>
				<!-- botão -->
			</div>
			<!-- login -->

			<div id="login-escolha">
				Fazer login como <a href="javascript:void(0);" class="login-link"
					id="login-link-empresa">Empresa</a> ou <a
					href="javascript:void(0);" class="login-link" id="login-link-aluno">Aluno</a>
			</div>
			<!-- login-escolha -->

			<div id="login-form">

				<form action="<%= contextPath %>/authenticationAction.do"
					class="login" method="post">
					<input type="hidden" name="method" value="login" />
					<div class="login-block">
						<label class="login-username"><bean:message	key="label.login.email" bundle="MYORG_RESOURCES" /></label><br />
						<input type="text" name="username" size="20" />
					</div>
					<div class="login-block">
						<label class="login-password"><bean:message	key="label.login.password" bundle="MYORG_RESOURCES" /></label><br />
						<input type="password" name="password" size="20" />
					</div>
					<bean:define id="loginLabel">
						<bean:message key="label.login.submit" bundle="MYORG_RESOURCES" />
					</bean:define>
					<input class="login-submit" type="submit" name="Submit"	value="<%= loginLabel %>" />
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
					<!-- HAS_CONTEXT -->
					<html:link page="/configuration.do?method=applicationConfiguration">
						<bean:message bundle="MYORG_RESOURCES"
							key="label.application.configuration" />
					</html:link>
					|
					<%	} %>
					<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES" />
					:
					<bean:write name="USER_SESSION_ATTRIBUTE" property="username" />
					|
					
					<logic:present name="virtualHost" property="helpLink">
						<logic:notEmpty name="virtualHost" property="helpLink">
							<bean:define id="helpUrl">
								<bean:write name="virtualHost" property="helpLink" />
							</bean:define>
							<a href="<%= helpUrl %>" target="_blank"><bean:message
									key="label.help.link" bundle="MYORG_RESOURCES" />
							</a> |  
					</logic:notEmpty>
					</logic:present>
					<html:link action="/authenticationAction.do?method=logout">
						<bean:message key="label.login.logout" bundle="MYORG_RESOURCES" />
					</html:link>
				</p>
			</div>
		</logic:present>


	</div>
	<!-- header -->


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
		
		<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
		<bean:define id="menuElements" name="context" property="menuElements"/>
		<logic:notEmpty name="menuElements">
			<bean:size name="menuElements" id="size"/>
			<logic:greaterThan name="size" value="1">
				<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
			</logic:greaterThan>
		</logic:notEmpty>
		
			<jsp:include page="<%= layoutContext.getSubMenuTop() %>" />

		</div><!-- topmenu -->
		
		
		<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
			<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do</bean:define>
				<div id="language">
					<a href="<%= languageUrl+"?method=firstPage&locale="+Language.pt.name() %>" >PT</a>
					<a href="<%= languageUrl+"?method=firstPage&locale="+Language.en.name() %>" >EN</a>
				</div>
		</logic:equal>
			
		

		<div id="conteudo">
			<jsp:include page="<%= layoutContext.getPageOperations() %>" />
			<logic:equal name="virtualHost" property="breadCrumbsEnabled"
				value="true">
				<jsp:include page="<%= layoutContext.getBreadCrumbs() %>" />
			</logic:equal>
			<jsp:include page="<%= layoutContext.getBody() %>" />
		</div><!-- conteudo -->


		<div id="footer">
			<p class="copyright">
				&copy;
				<dt:format pattern="yyyy">
					<dt:currentTime />
				</dt:format>
				<bean:write name="virtualHost" property="applicationCopyright" />
			</p>
		</div><!-- footer -->

	</div><!-- wrap -->


</body>
</html:html>


