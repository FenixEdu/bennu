<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@page import="myorg.domain.VirtualHost"%>

<title><bean:write name="virtualHost" property="applicationTitle"/></title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<%
	final String contextPath = request.getContextPath();
%>

<bean:define id="theme" name="virtualHost" property="theme.name"/>
<bean:define id="iconUrl"><%= contextPath %>/home.do?method=favico&virtualHostId=<bean:write name="virtualHost" property="OID"/></bean:define>

<logic:present name="virtualHost" property="favicon">
	<link rel="shortcut icon" type="image/x-icon" href="<%= iconUrl %>"/>
</logic:present>

<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/screen.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/print.css" media="print"/>
<%-- 
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/layout.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/print.css" media="print"/>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/contents.css" media="screen, print"/>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/dragContents.css" media="screen"/>
 --%>
 