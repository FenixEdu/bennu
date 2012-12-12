<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@page import="pt.ist.bennu.core.domain.VirtualHost"%>

<title>
	<logic:notEmpty name="virtualHost" property="htmlTitle">
		<bean:write name="virtualHost" property="htmlTitle"/>
	</logic:notEmpty>
	<bean:write name="virtualHost" property="applicationTitle.content"/>
	<logic:present name="virtualHost" property="applicationSubTitle">
	 - <bean:write name="virtualHost" property="applicationSubTitle.content"/>
	</logic:present>
</title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<!--[if IE 8]>
<meta http-equiv="X-UA-Compatible" content="IE=8">
<![endif]-->
<meta http-equiv="X-UA-Compatible" content="chrome=1"/>

<%
	final String contextPath = request.getContextPath();
%>

<bean:define id="theme" name="virtualHost" property="theme.name"/>
<bean:define id="iconUrl"><%= contextPath %>/home.do?method=favico&virtualHostId=<bean:write name="virtualHost" property="externalId"/></bean:define>

<logic:present name="virtualHost" property="favicon">
	<link rel="shortcut icon" type="image/x-icon" href="<%= iconUrl %>"/>
</logic:present>

<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/screen.css" media="screen"/>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme %>/print.css" media="print"/>

<link rel="stylesheet" type="text/css" href="<%=contextPath + "/head.css"%>">

<script src="<%= contextPath%>/javaScript/jquery.js" type="text/javascript"></script>
<script src="<%= contextPath%>/javaScript/jquery-ui.js" type="text/javascript"></script>
<script src="<%= contextPath%>/javaScript/jquery.form.js" type="text/javascript"></script>
<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/scriptingUtils.js"%>"></script>
<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/liveValidation.js" %>"></script> 
