<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>
<html:html xhtml="true">

<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
%>

<head>
	<logic:iterate id="head" collection="<%= layoutContext.getHead() %>" type="java.lang.String">
		<jsp:include page="<%= head %>"/>
	</logic:iterate>
</head>

<body>

<div id="container">

	<div id="header">
		<jsp:include page="<%= layoutContext.getPageHeader() %>"/>
	</div>

	<div id="mainnav">
			<jsp:include page="<%= layoutContext.getMenuTop() %>"/>
		<div class="c1"></div>
		<div class="c2"></div>
	</div>
	
	
	<div id="container2">
		<div id="secondarynav">
				<jsp:include page="<%= layoutContext.getSubMenuTop() %>"/>
			<div class="c1"></div>
			<div class="c2"></div>
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

	<div class="cont_c1"></div>
	<div class="cont_c2"></div>

</div>
</div>

</body>
</html:html>
