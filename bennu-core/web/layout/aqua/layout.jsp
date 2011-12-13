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
<body>


	<header id="header">
		<div class="container">
			<h1>
				<a href="<%= contextPath %>">
					<bean:write name="virtualHost" property="applicationTitle"/>
				</a>
			</h1>
			
	
			<nav id="perfil">
				<p>
					<jsp:include page="<%= layoutContext.getConfigurationLink() %>"/>
				</p>
				<jsp:include page="<%= layoutContext.getLogin() %>"/>
				<jsp:include page="<%= layoutContext.getHelpLink() %>"/>
			</nav>
		
			<nav id="tabs">			
				<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
				<bean:define id="menuElements" name="context" property="menuElements"/>
				<logic:notEmpty name="menuElements">
					<bean:size name="menuElements" id="size"/>
					<logic:greaterThan name="size" value="1">
						<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
					</logic:greaterThan>
				</logic:notEmpty>

				
				<jsp:include page="<%= layoutContext.getSubMenuTop() %>" />
			</nav><!-- tabs -->
		
		</div><!-- container -->
	</header><!-- header -->
	
	<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
		<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do</bean:define>
			<div id="language">
				<jsp:include page="<%= layoutContext.getLanguageSelection() %>"/>
			</div>
	</logic:equal>
	
	<section id="content">
		<div class="container">	

			<div id="conteudo">
				<jsp:include page="<%= layoutContext.getPageOperations() %>" />
				<jsp:include page="<%= layoutContext.getBreadCrumbs() %>" />
				<jsp:include page="<%= layoutContext.getBody() %>" />
			</div><!-- conteudo -->

		</div><!-- container_12 -->
	
		<footer id="content-footer"></footer>
	</section><!-- container_12 -->
	
	<footer id="footer">
		<div class="container">
			<jsp:include page="<%= layoutContext.getFooter() %>"/>
		</div>
	</footer><!-- footer -->

</body>
</html:html>