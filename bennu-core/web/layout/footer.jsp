<%@page import="pt.ist.fenixWebFramework.Config.CasConfig"%>
<%@page import="pt.ist.fenixWebFramework.FenixWebFramework"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/taglibs-datetime.tld" prefix="dt" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<div class="c1"></div>
<div class="c2"></div>
<div class="c3"></div>
<div class="c4"></div>

<p>
	&copy;<dt:format pattern="yyyy"><dt:currentTime/></dt:format> <bean:write name="virtualHost" property="applicationCopyright"/>
</p>

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

