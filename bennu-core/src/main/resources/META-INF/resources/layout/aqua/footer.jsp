<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.joda.org/joda/time/tags" prefix="joda" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

    <% pageContext.setAttribute("now", new org.joda.time.DateTime()); %>
	&copy;<joda:format value="${now}" pattern="yyyy"/> <bean:write name="virtualHost" property="applicationCopyright"/>


<%
	final String contextPath = request.getContextPath();
	final String serverName = request.getServerName();
	final CasConfig casConfig = FenixWebFramework.getConfig().getCasConfig(serverName);
	final boolean isCasEnabled = casConfig != null && casConfig.isCasEnabled();
%>
<% if (isCasEnabled) {%>
	<logic:present name="USER_SESSION_ATTRIBUTE">
		<iframe src="https://fenix.ist.utl.pt/privado" width="0%" height="0%" style="visibility: hidden;"></iframe>
	</logic:present>
<% } %>

