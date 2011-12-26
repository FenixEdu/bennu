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
	<script type="text/javascript" src="<%= contextPath %>/CSS/aqua/adminbar.js"></script>
</head>
<body>
	<jsp:include page="/layout/aqua/adminbar.jsp"/>

	<header id="header">
		<div class="container">
			<h1>
				<a href="<%= contextPath %>">
					<logic:present name="virtualHost" property="logo">
						<html:img styleClass="logo" action="/home.do?method=logo" paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId" />
					</logic:present>
					<bean:write name="virtualHost" property="applicationTitle"/>
				</a>
			</h1>

			<nav id="perfil">
				<ul>
					<li><jsp:include page="<%= layoutContext.getProfileLink() %>" /></li>
					<li><jsp:include page="<%= layoutContext.getHelpLink() %>"/></li>
					<li><jsp:include page="<%= layoutContext.getLogin() %>"/></li>
				</ul>
			</nav>

			<jsp:include page="<%= layoutContext.getGoogleSearch() %>"/>
		
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
			
			<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
				<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do</bean:define>
					<div id="language">
						<jsp:include page="<%= layoutContext.getLanguageSelection() %>"/>
					</div>
			</logic:equal>
			
		</div><!-- container -->
	</header><!-- header -->

	
	<div class="container">	
		<section id="content">
				<div id="conteudo">
					<jsp:include page="<%= layoutContext.getPageOperations() %>" />
					<jsp:include page="<%= layoutContext.getBreadCrumbs() %>" />
					<jsp:include page="<%= layoutContext.getBody() %>" />
				</div><!-- conteudo -->
		</section><!-- content -->
	</div><!-- container_12 -->
	
	<footer id="footer">
		<div class="container">
			<p>
				<logic:present name="virtualHost" property="applicationSubTitle">
					<span class="subtitle"><bean:write name="virtualHost" property="applicationSubTitle"/></span>
				</logic:present>
				<jsp:include page="<%= layoutContext.getFooter() %>"/>
			</p>
		</div>
	</footer><!-- footer -->

</body>
</html:html>