<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%><html:html xhtml="true">
<head>
	<tiles:insert page="/layout/head.jsp"/>
</head>

<body>
<div class="errorcontainer">
	<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request);
	%>
	<div id="header">
		<jsp:include page="<%= layoutContext.getPageHeader() %>"/>
	</div>

	<div class="errortxt">
		<h2>
			<span><bean:message key="messages.exception.errorOccured" bundle="MYORG_RESOURCES"/></span>
		</h2>
	</div>
</div>
</body>
</html:html>
