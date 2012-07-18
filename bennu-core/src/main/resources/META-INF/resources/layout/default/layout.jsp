<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="pt.ist.bennu.core._development.PropertiesManager"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@page import="pt.ist.bennu.core.presentationTier.LayoutContext"%>
<%@page import="pt.ist.bennu.core.presentationTier.actions.ContextBaseAction"%>
<html:html xhtml="true">

<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
%>

<head>
	<logic:iterate id="head" collection="<%= layoutContext.getHead() %>" type="java.lang.String">
		<jsp:include page="<%= head %>"/>
	</logic:iterate>
</head>

<body class="<%= PropertiesManager.getProperty("development.mode") != null && PropertiesManager.getProperty("development.mode").equalsIgnoreCase("true") ? "top_dev" : "top"%>">

<div id="container">

	<div id="header">
		<div id="logo">
			<logic:present name="virtualHost" property="logo">
		    	<bean:define id="logoUrl"><%= request.getContextPath() %>/home.do?method=logo&virtualHostId=<bean:write name="virtualHost" property="externalId"/></bean:define>
				<html:img src='<%= logoUrl %>'/>
			</logic:present>
		
			<div id="text">
				<h1><bean:write name="virtualHost" property="applicationTitle"/></h1>
				<p><bean:write name="virtualHost" property="applicationSubTitle"/></p>
			</div>
		</div>
	
		<div class="clear"></div>
		
		<div id="supportnav">
			<jsp:include page="<%= layoutContext.getConfigurationLink() %>"/>
			
			<jsp:include page="<%= layoutContext.getHelpLink() %>"/>
		
			<jsp:include page="<%= layoutContext.getLogin() %>"/>
		</div>
	
		<div id="headerforms">
			<jsp:include page="<%= layoutContext.getLanguageSelection() %>"/>
		
			<jsp:include page="<%= layoutContext.getGoogleSearch() %>"/>
		</div>
	</div>

	<div id="mainnav">
		<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
	</div>
	
	<div class="clear"></div>
	
	<div id="container2">
		<div id="secondarynav">
				<jsp:include page="<%= layoutContext.getSubMenuTop() %>"/>
		</div>
		

		<div id="container3">
			<div id="content">
				<jsp:include page="<%= layoutContext.getPageOperations() %>"/>
				<logic:equal name="virtualHost" property="breadCrumbsEnabled" value="true">
					<jsp:include page="<%= layoutContext.getBreadCrumbs() %>"/>
				</logic:equal>
				<jsp:include page="<%= layoutContext.getBody() %>"/>
			</div>
		</div>

	<div id="footer">
		<jsp:include page="<%= layoutContext.getFooter() %>"/>
	</div>

</div>
<!-- #container2 -->

</div>
<!-- #container1 -->


</body>
</html:html>
