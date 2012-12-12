<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@page import="pt.ist.bennu.core.presentationTier.LayoutContext"%>
<%@page import="pt.ist.bennu.core.presentationTier.actions.ContextBaseAction"%>


<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
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
	
	<script type="text/javascript" src="<%= contextPath %>/CSS/theme_test_beta/js/script.js"></script>
	
</head>


<body class="top">

	<div id="header" class="container_12">

		<logic:present name="virtualHost" property="logo">
	    	<bean:define id="logoUrl"><%= request.getContextPath() %>/home.do?method=logo&virtualHostId=<bean:write name="virtualHost" property="externalId"/></bean:define>
	    	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><!-- NO_CHECKSUM --><a href="<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/" + request.getContextPath() %>">
				<html:img src='<%= logoUrl %>'/>
	    	</a>
		</logic:present>
		
		<div id="text">
			<h1><bean:write name="virtualHost" property="applicationTitle"/></h1>
			<p><bean:write name="virtualHost" property="applicationSubTitle"/></p>
		</div>

		<jsp:include page="<%= layoutContext.getLogin() %>"/>	

	</div>
	<!-- header -->


	<%--
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
		
		<bean:define id="context" type="pt.ist.bennu.core.presentationTier.Context" name="_CONTEXT_"/>
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
				<div id="language" style="display:none;">
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


