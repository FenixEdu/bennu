<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%>
<%@page import="pt.utl.ist.fenix.tools.util.FileUtils"%>
<%@page import="java.io.InputStream"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/taglibs-datetime.tld" prefix="dt" %>

<html:xhtml/>

<ul>
	<li>
		<html:link page="/configuration.do?method=applicationConfiguration">
			<bean:message key="link.back" bundle="MYORG_RESOURCES"/>
		</html:link>
	</li>
</ul>
<%
	final InputStream inputStream = this.getClass().getResourceAsStream("/.build.version");
%>

BuildVersion: <%= FileUtils.readFile(inputStream).toString() %>

	
	<ul>
		<li>
			<bean:message bundle="MYORG_RESOURCES" key="label.server.name"/>: <%= request.getServerName() %>
		</li>
		<li>
			<bean:message bundle="MYORG_RESOURCES" key="label.real.server.name"/>: <%= System.getenv("HOSTNAME") %>
		</li>
	</ul>
	
	<strong><bean:message bundle="MYORG_RESOURCES" key="label.system.properties"/></strong>

	<% request.setAttribute("systemProperties", System.getProperties()); %>
	<ul>
	<logic:iterate id="property" name="systemProperties">
		<li><bean:write name="property"/></li>
	</logic:iterate>
	</ul>
	
	<strong><bean:message bundle="MYORG_RESOURCES" key="label.environment.properties"/></strong>
	
<% request.setAttribute("systemEnv", System.getenv()); %>
	<ul>
		<logic:iterate id="property" name="systemEnv">
			<li><bean:write name="property" property="key"/>=<bean:write name="property" property="value"/></li>
		</logic:iterate>
	</ul>

<strong><bean:message bundle="MYORG_RESOURCES" key="label.request.headers"/></strong>
<ul>
<%
	for (final Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
	    final Object o = e.nextElement();
	    %>
	    <li><strong>
	    <%= o.toString() %>
	    </strong>
	    =
	    <%= request.getHeader(o.toString()) %>
	   	</li>
	    <%
	}
%>
</ul>