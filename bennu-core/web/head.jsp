<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<title><bean:write name="myOrg" property="applicationTitle"/></title>

<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<%
	final String contextPath = request.getContextPath();
%>
<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/layout.css" media="screen">
