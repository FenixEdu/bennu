<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.security.User"%>

<logic:present name="USER_SESSION_ATTRIBUTE">
<%
	final User user = (User) request.getSession(false).getAttribute("USER_SESSION_ATTRIBUTE");
	if (user.hasRole("pt.ist.bennu.core.domain.RoleType.MANAGER")) {
%>
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=applicationConfiguration">
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>
		</html:link> |
<%	} %>
</logic:present>
