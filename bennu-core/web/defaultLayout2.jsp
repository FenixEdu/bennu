<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<html:html xhtml="true">
<head>
	<tiles:insert attribute="head" ignore="true"/>
</head>

<body>

<div id="container" class="top">

	<div id="header">
		<tiles:insert attribute="pageHeader" ignore="true"/>
	</div>

	<div id="mainnav">
		<tiles:insert attribute="topBar"/>
		<div class="c1"></div>
		<div class="c2"></div>
	</div>

	<div id="container2">

		<div id="secnav">
			<tiles:insert attribute="sideBar"/>
			<div class="c1"></div>
			<div class="c2"></div>
		</div>

	<div id="container3">

		<div id="content">
		  	<tiles:insert attribute="body" ignore="true"/>
		</div> <!-- content -->

	</div> <!-- container3 -->

	<div id="footer">
		<tiles:insert attribute="footer"/>
	</div>

	<div class="cont_c1"></div>
	<div class="cont_c2"></div>

</div> <!-- container2 -->
</div> <!-- container -->

</body>
</html:html>
