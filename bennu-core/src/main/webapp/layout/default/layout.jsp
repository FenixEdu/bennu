<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@page import="org.joda.time.LocalDate"%>
<%@page import="org.joda.time.DateTimeFieldType"%>
<%@page import="org.joda.time.DateTimeField"%>
<%@page import="org.joda.time.DateTime"%>
<%@page import="pt.ist.bennu.core._development.PropertiesManager"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean"
	prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html"
	prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic"
	prefix="logic"%>
<%@page import="pt.ist.bennu.core.presentationTier.LayoutContext"%>
<%@page
	import="pt.ist.bennu.core.presentationTier.actions.ContextBaseAction"%>
<html:html xhtml="true">

<%
	final LayoutContext layoutContext = (LayoutContext) ContextBaseAction
				.getContext(request);
%>

<head>
<logic:iterate id="head" collection="<%=layoutContext.getHead()%>"
	type="java.lang.String">
	<jsp:include page="<%=head%>" />
</logic:iterate>
</head>

<body
	class="<%=PropertiesManager.getProperty("development.mode") != null
						&& PropertiesManager.getProperty("development.mode")
								.equalsIgnoreCase("true") ? "top_dev" : "top"%>">

	<%
	LocalDate currentDate = new LocalDate();
	int month = currentDate.getMonthOfYear();
		if (PropertiesManager.getProperty("development.mode") != null
					&& PropertiesManager.getProperty("development.mode")
							.equalsIgnoreCase("true") && (month == 11 || month == 12 || month == 1 || month == 0)  ) {
	%>
	<script type="text/javascript">
	/*
	Snow Fall 1 - no images - Java Script
	Visit http://rainbow.arch.scriptmania.com/scripts/
	  for this script and many more
	*/

	// Set the number of snowflakes (more than 30 - 40 not recommended)
	var snowmax=35

	// Set the colors for the snow. Add as many colors as you like
	var snowcolor=new Array("#aaaacc","#ddddff","#ccccdd","#f3f3f3","#f0ffff")

	// Set the fonts, that create the snowflakes. Add as many fonts as you like
	var snowtype=new Array("Times","Arial","Times","Verdana")

	// Set the letter that creates your snowflake (recommended: * )
	var snowletter="*"

	// Set the speed of sinking (recommended values range from 0.3 to 2)
	var sinkspeed=0.6

	// Set the maximum-size of your snowflakes
	var snowmaxsize=30

	// Set the minimal-size of your snowflakes
	var snowminsize=8

	// Set the snowing-zone
	// Set 1 for all-over-snowing, set 2 for left-side-snowing
	// Set 3 for center-snowing, set 4 for right-side-snowing
	var snowingzone=1

	///////////////////////////////////////////////////////////////////////////
	// CONFIGURATION ENDS HERE
	///////////////////////////////////////////////////////////////////////////


	// Do not edit below this line
	var snow=new Array()
	var marginbottom
	var marginright
	var timer
	var i_snow=0
	var x_mv=new Array();
	var crds=new Array();
	var lftrght=new Array();
	var browserinfos=navigator.userAgent
	var ie5=document.all&&document.getElementById&&!browserinfos.match(/Opera/)
	var ns6=document.getElementById&&!document.all
	var opera=browserinfos.match(/Opera/)
	var browserok=ie5||ns6||opera

	function randommaker(range) {
	        rand=Math.floor(range*Math.random())
	    return rand
	}

	function initsnow() {
	        if (ie5 || opera) {
	                marginbottom = document.body.scrollHeight
	                marginright = document.body.clientWidth-15
	        }
	        else if (ns6) {
	                marginbottom = document.body.scrollHeight
	                marginright = window.innerWidth-15
	        }
	        var snowsizerange=snowmaxsize-snowminsize
	        for (i=0;i<=snowmax;i++) {
	                crds[i] = 0;
	            lftrght[i] = Math.random()*15;
	            x_mv[i] = 0.03 + Math.random()/10;
	                snow[i]=document.getElementById("s"+i)
	                snow[i].style.fontFamily=snowtype[randommaker(snowtype.length)]
	                snow[i].size=randommaker(snowsizerange)+snowminsize
	                snow[i].style.fontSize=snow[i].size+'px';
	                snow[i].style.color=snowcolor[randommaker(snowcolor.length)]
	                snow[i].style.zIndex=1000
	                snow[i].sink=sinkspeed*snow[i].size/5
	                if (snowingzone==1) {snow[i].posx=randommaker(marginright-snow[i].size)}
	                if (snowingzone==2) {snow[i].posx=randommaker(marginright/2-snow[i].size)}
	                if (snowingzone==3) {snow[i].posx=randommaker(marginright/2-snow[i].size)+marginright/4}
	                if (snowingzone==4) {snow[i].posx=randommaker(marginright/2-snow[i].size)+marginright/2}
	                snow[i].posy=randommaker(2*marginbottom-marginbottom-2*snow[i].size)
	                snow[i].style.left=snow[i].posx+'px';
	                snow[i].style.top=snow[i].posy+'px';
	        }
	        movesnow()
	}

	function movesnow() {
	        for (i=0;i<=snowmax;i++) {
	                crds[i] += x_mv[i];
	                snow[i].posy+=snow[i].sink
	                snow[i].style.left=snow[i].posx+lftrght[i]*Math.sin(crds[i])+'px';
	                snow[i].style.top=snow[i].posy+'px';

	                if (snow[i].posy>=marginbottom-2*snow[i].size || parseInt(snow[i].style.left)>(marginright-3*lftrght[i])){
	                        if (snowingzone==1) {snow[i].posx=randommaker(marginright-snow[i].size)}
	                        if (snowingzone==2) {snow[i].posx=randommaker(marginright/2-snow[i].size)}
	                        if (snowingzone==3) {snow[i].posx=randommaker(marginright/2-snow[i].size)+marginright/4}
	                        if (snowingzone==4) {snow[i].posx=randommaker(marginright/2-snow[i].size)+marginright/2}
	                        snow[i].posy=0
	                }
	        }
	        var timer=setTimeout("movesnow()",50)
	}

	for (i=0;i<=snowmax;i++) {
	        document.write("<span id='s"+i+"' style='position:absolute;top:-"+snowmaxsize+"'>"+snowletter+"</span>")
	}


	var globalTypedString="";
	var globalCounter=0;
	if (browserok) {
		initsnow();
	}
	</script>
	<%
		}
	%>

	<div id="container">

		<div id="header">
			<div id="logo">
				<logic:present name="virtualHost" property="logo">
					<bean:define id="logoUrl"><%=request.getContextPath()%>/home.do?method=logo&virtualHostId=<bean:write
							name="virtualHost" property="externalId" />
					</bean:define>
					<html:img src='<%=logoUrl%>' />
				</logic:present>

				<div id="text">
					<h1>
						<bean:write name="virtualHost" property="applicationTitle" />
					</h1>
					<p>
						<bean:write name="virtualHost" property="applicationSubTitle" />
					</p>
				</div>
			</div>

			<div class="clear"></div>

			<div id="supportnav">
				<jsp:include page="<%=layoutContext.getConfigurationLink()%>" />

				<jsp:include page="<%=layoutContext.getHelpLink()%>" />

				<jsp:include page="<%=layoutContext.getLogin()%>" />
			</div>

			<div id="headerforms">
				<jsp:include page="<%=layoutContext.getLanguageSelection()%>" />

				<jsp:include page="<%=layoutContext.getGoogleSearch()%>" />
			</div>
		</div>

		<div id="mainnav">
			<jsp:include page="<%=layoutContext.getMenuTop()%>" />
		</div>

		<div class="clear"></div>

		<div id="container2">
			<div id="secondarynav">
				<jsp:include page="<%=layoutContext.getSubMenuTop()%>" />
			</div>


			<div id="container3">
				<div id="content">
					<jsp:include page="<%=layoutContext.getPageOperations()%>" />
					<logic:equal name="virtualHost" property="breadCrumbsEnabled"
						value="true">
						<jsp:include page="<%=layoutContext.getBreadCrumbs()%>" />
					</logic:equal>
					<jsp:include page="<%=layoutContext.getBody()%>" />
				</div>
			</div>

			<div id="footer">
				<jsp:include page="<%=layoutContext.getFooter()%>" />
			</div>

		</div>
		<!-- #container2 -->

	</div>
	<!-- #container1 -->


</body>
</html:html>
