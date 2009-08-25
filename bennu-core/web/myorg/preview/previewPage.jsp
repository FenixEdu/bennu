<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="myorg.domain.VirtualHost"%>
<%@page import="myorg.presentationTier.LayoutContext"%>
<%@page import="myorg.presentationTier.actions.ContextBaseAction"%>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.utl.ist.fenix.tools.util.i18n.Language"%>
<%@page import="myorg.domain.Theme"%>

<html:html xhtml="true">

<head>
	<title><bean:write name="virtualHost" property="applicationTitle"/></title>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<%
		final String contextPath = request.getContextPath();
		final Theme theme = (Theme) request.getAttribute("theme");
	%>

	<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme.getName() %>/screen.css" media="screen"/>
	<link rel="stylesheet" type="text/css" href="<%= contextPath %>/CSS/<%= theme.getName() %>/print.css" media="print"/>
</head>

<body>

<div id="container">

	<div id="header">
		<table width="100%">
  			<tr>
    			<td rowspan="2" width="60%" valign="top">
					<h1><bean:write name="virtualHost" property="applicationTitle"/></h1>
					<p><bean:write name="virtualHost" property="applicationSubTitle"/></p>
    			</td>
    			<td align="right" nowrap="nowrap" width="40%">
					<%
						final String serverName = request.getServerName();
						final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
						final boolean isCasEnabled = casConfig != null && casConfig.isCasEnabled();
					%>
					<logic:present name="USER_SESSION_ATTRIBUTE">
						<div class="login">
							<!-- HAS_CONTEXT --><html:link href="#">
								<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>
							</html:link> |
							<logic:present name="virtualHost" property="helpLink">
								<logic:notEmpty name="virtualHost" property="helpLink">
									<bean:define id="helpUrl"><bean:write name="virtualHost" property="helpLink"/></bean:define>
									<a href="#" target="_blank"><bean:message key="label.help.link" bundle="MYORG_RESOURCES"/></a> |  
								</logic:notEmpty>
							</logic:present>
							<bean:message key="label.login.loggedInAs" bundle="MYORG_RESOURCES"/>: <bean:write name="USER_SESSION_ATTRIBUTE" property="username"/> |
							<html:link href="#"><bean:message key="label.login.logout" bundle="MYORG_RESOURCES"/></html:link>
						</div>
					</logic:present>
				</td>
				</tr>
				<tr>
					<td align="right" nowrap="nowrap" width="40%">
						<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
							<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do?method=previewTheme&themeId=<%= theme.getExternalId() %></bean:define>
							<form action="<%= languageUrl %>" method="post" class="login">
								<input type="hidden" name="method" value="viewPage" />
								<logic:present name="selectedNode">
									<bean:define id="arg" name="selectedNode" property="externalId" type="java.lang.String"/>
									<input type="hidden" name="nodeOid" value="<%= arg %>"/>
								</logic:present>
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<select name="locale" onchange="this.form.submit();">
									<% final String country = Language.getLocale().getCountry(); %>
									<% for (Language language : Language.values()) {
										if (language == Language.getLanguage()) {
							    			%>
								    			<option value="<%= language.name() %>_<%= country %>" selected="selected">
							    					<%= language.name() %>
							    				</option>
							    			<%
										} else {
								    		%>
							    				<option value="<%= language.name() %>_<%= country %>">
								    				<%= language.name() %>
							    				</option>
							    			<%							    
										}
									} %>
								</select>
								<input class=" button" type="submit" name="Submit" value="Ok" />
							</form>
						</logic:equal>
						<logic:equal name="virtualHost" property="googleSearchEnabled" value="true">	
							<bean:define id="site" name="virtualHost" property="hostname"/>
							<bean:define id="searchUrl"><%= request.getContextPath() %>/home.do?method=previewTheme&themeId=<%= theme.getExternalId() %></bean:define>
							<form method="post" action="<%= searchUrl %>">
								<input type="text" id="q" name="q" value="Search..." />
								<input class=" button" type="submit" name="Submit" value="Google" />
							</form>
							<!-- END_BLOCK_HAS_CONTEXT -->
						</logic:equal>
				</td>
			</tr>
		</table>
	</div>

	<div id="mainnav">
		<logic:equal name="theme" property="type" value="TOP">
			<ul>
				<li>
					<html:link href="#">
						<span>Link 1</span>
						<div class="lic1"></div>
					</html:link>
				</li>
				<li class="selected">
					<html:link href="#">
						<span>Link 2</span>
					</html:link>
				</li>
				<li>
					<html:link href="#">
						<span>Link 3</span>
					</html:link>
				</li>
			</ul>
		</logic:equal>
		<div class="c1"></div>
		<div class="c2"></div>
	</div>
	
	
	<div id="container2">
		<div id="secondarynav">
			<logic:equal name="theme" property="type" value="TOP">
				<ul>
					<li class="navsublist">
						<!-- HAS_CONTEXT --><html:link href="#">
							<span>Sub Link 1</span>
						</html:link>
					</li>
					<span class="bar">|</span>
					<li class="navsublist">
						<!-- HAS_CONTEXT --><html:link href="#">
							<span>Sub Link 2</span>
						</html:link>
					</li>
					<span class="bar">|</span>
					<li class="navsublist">
						<!-- HAS_CONTEXT --><html:link href="#">
							<span>Sub Link 3</span>
						</html:link>
					</li>
				</ul>
			</logic:equal>
			<logic:equal name="theme" property="type" value="SIDE">
				<div id="navlist">
					<ul>
						<li>
							<div>
								<!-- HAS_CONTEXT --><html:link href="#">
									Link 1
								</html:link>
							</div>
						</li>
						<li>
							<div>
								<!-- HAS_CONTEXT --><html:link href="#" styleClass="navlistselected">
									Link 2
								</html:link>
							</div>
						</li>
						<li class="navsublist">
							<!-- HAS_CONTEXT --><html:link href="#">
								Sub Link 1
							</html:link>
						</li>
						<li class="navsublist">
							<!-- HAS_CONTEXT --><html:link href="#" styleClass="navlistselected">
								Sub Link 2
							</html:link>
						</li>
						<li class="navsublist">
							<!-- HAS_CONTEXT --><html:link href="#">
								Sub Link 3
							</html:link>
						</li>
						<li>
							<div>
								<!-- HAS_CONTEXT --><html:link href="#">
									Link 3
								</html:link>
							</div>
						</li>
					</ul>
				</div>
			</logic:equal>
			<div class="c1"></div>
			<div class="c2"></div>
		</div>
		

		<div id="container3">
			<div id="content">
				<fr:view name="myOrg" property="virtualHosts" schema="virtualHost.application.configuration.summary">
					<fr:layout name="tabular">
						<fr:property name="classes" value="tstyle2" />
					</fr:layout>
				</fr:view>
			</div>
		</div>

	<div id="footer">
		<div class="c1"></div>
		<div class="c2"></div>
		<div class="c3"></div>
		<div class="c4"></div>
		<p>
			<a href="#"><bean:message bundle="MYORG_RESOURCES" key="label.application.valid.xhtml"/></a>
			|
			<a href="#"><bean:message bundle="MYORG_RESOURCES" key="label.application.valid.css"/></a>
			<strong>
				&copy;<dt:format pattern="yyyy"><dt:currentTime/></dt:format> <bean:write name="virtualHost" property="applicationCopyright"/>
			</strong>
		</p>
	</div>

	<div class="cont_c1"></div>
	<div class="cont_c2"></div>

</div>
</div>

</body>
</html:html>
