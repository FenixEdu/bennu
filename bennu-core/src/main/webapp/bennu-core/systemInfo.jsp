<%@page import="java.util.Properties"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Calendar"%>
<%@page import="pt.utl.ist.fenix.tools.util.FileUtils"%>
<%@page import="java.io.InputStream"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<html:xhtml/>

<ul>
	<li>
		<html:link page="/configuration.do?method=applicationConfiguration">
			<bean:message key="link.back" bundle="MYORG_RESOURCES"/>
		</html:link>
	</li>
</ul>
<%
	Properties prop = new Properties();
	final InputStream inputStream = this.getClass().getResourceAsStream("/META-INF/resources/build.properties");
	prop.load(inputStream);
%>

BuildVersion: <%= prop.getProperty("build.timestamp").toString() %>
<logic:notEmpty name="jars">
<ul>
	<logic:iterate id="jar" name="jars">
		<li><bean:write name="jar" property="name"/></li>
	</logic:iterate>
</ul>
</logic:notEmpty>

	
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
