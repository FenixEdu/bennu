<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<html:html xhtml="true">

	<% final LayoutContext layoutContext = (LayoutContext) ContextBaseAction.getContext(request); %>

	<head>
		<% for (final String head : layoutContext.getHead()) { %>
				<jsp:include page="<%= head %>"/>
		<% } %>
	</head>

	<body>
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

	<div id="container2">
		<div id="container3">
			<div class="errortxt">
				<h2>
					<span>
						<bean:message key="messages.exception.errorOccured" bundle="MYORG_RESOURCES"/>
					</span>
				</h2>
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
