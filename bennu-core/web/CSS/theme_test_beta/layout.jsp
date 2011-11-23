<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>

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
	
	<!--[if IE 7]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie7.css" />
	<![endif]-->
	<!--[if IE 8]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie8.css" />
	<![endif]-->
	<!--[if IE 9]>
	        <link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/theme_test_beta/ie9.css" />
	<![endif]-->
	
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
	<script type="text/javascript" src="<%= contextPath %>/CSS/sotis/js/script.js"></script>
	
</head>
<body class="top">


	<header id="header">
		<div class="container_12">
			<h1>
				<a href="<%= contextPath %>" title="Job Bank">
					SOTIS
				</a>
			</h1>
			
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


				<div id="login-form">

					<jsp:include page="<%= layoutContext.getLogin() %>"/>


						<a href="<%= contextPath %>/enterprise.do?method=prepareToPasswordRecover" class="esqueceu-password"><bean:message bundle="JOB_BANK_RESOURCES" key="label.jobBank.login.forgotPassword"/></a>

				</div>

				<% } %>
			</logic:notPresent>
			<% } %>
			
	
		<logic:present name="USER_SESSION_ATTRIBUTE">	
			<nav id="perfil">
				
				<ul>
					<li><jsp:include page="<%= layoutContext.getConfigurationLink() %>"/></li>
					<li><jsp:include page="<%= layoutContext.getLogin() %>"/></li>
					<li><jsp:include page="<%= layoutContext.getHelpLink() %>"/></li>
				</ul>
			</nav>
		</logic:present>

		</div>
	</header><!-- header -->
	
	<section id="content">
		<div class="container_12">

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
						<a href="<%= languageUrl+"?method=firstPage&locale="+Language.pt.name() %>" class="pt">PT</a>
						<a href="<%= languageUrl+"?method=firstPage&locale="+Language.en.name() %>">EN</a>
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

		</div><!-- container_12 -->
		<footer id="content-footer"></footer>
	</section>
	
	<div id="footer">
		<div class="container_12">
			<p>
				&copy;
				<dt:format pattern="yyyy">
					<dt:currentTime />
				</dt:format>
				<bean:write name="virtualHost" property="applicationCopyright" />
			</p>
		</div>
	</div><!-- footer -->

</body>
</html:html>


