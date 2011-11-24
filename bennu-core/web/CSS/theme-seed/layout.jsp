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

<%-- Application Logo --%>
<html:img styleClass="logo" action="/home.do?method=logo" paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId" />

<%-- Application Title --%>
<div class="title"><bean:write name="virtualHost" property="applicationTitle"/></div>

<%-- Application Sub Title --%>
<div class="subtitle"><bean:write name="virtualHost" property="applicationSubTitle"/></div>

<%-- Manager Configuration Link --%>
<jsp:include page="<%= layoutContext.getConfigurationLink() %>"/>

<%-- Help Link --%>
<jsp:include page="<%= layoutContext.getHelpLink() %>"/>

<%-- Login logic --%>
<jsp:include page="<%= layoutContext.getLogin() %>"/>

<%-- Language Selection --%>
<jsp:include page="<%= layoutContext.getLanguageSelection() %>"/>

<%-- Google Search Form --%>
<jsp:include page="<%= layoutContext.getGoogleSearch() %>"/>

<%-- Application Menu --%>
<jsp:include page="<%= layoutContext.getMenuTop() %>"/>

<%-- Application Sub Menu --%>
<jsp:include page="<%= layoutContext.getSubMenuTop() %>"/>

<%-- Page Operations Section --%>
<jsp:include page="<%= layoutContext.getPageOperations() %>"/>

<%-- Breadcrumbs Section --%>
<jsp:include page="<%= layoutContext.getBreadCrumbs() %>"/>

<%-- Body Section --%>
<jsp:include page="<%= layoutContext.getBody() %>"/>

<%-- Footer Section --%>
<jsp:include page="<%= layoutContext.getFooter() %>"/>

</html:html>
