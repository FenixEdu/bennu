<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<html:html xhtml="true">
<head>
	<tiles:insert attribute="head" ignore="true"/>
</head>

<body>

<div id="container">

	<div id="header">
		<tiles:insert attribute="pageHeader" ignore="true"/>
	</div>

	<div id="container2">
		<div id="navigation">
			<tiles:insert attribute="sideBarLeft"/>
		</div>

<!-- 
		<div id="sidebar">
			<tiles:insert attribute="sideBar"/>
		</div>
 -->

		<div id="content">
			<tiles:insert attribute="pageOperations" ignore="true"/>
		  	<tiles:insert attribute="body" ignore="true"/>
		</div>
	</div>

	<div id="footer">
		<tiles:insert attribute="footer"/>
	</div>

	<div class="cont_c1"></div>
	<div class="cont_c2"></div>

</div>

</body>
</html:html>
