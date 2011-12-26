<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.security.User"%>

<logic:present name="USER_SESSION_ATTRIBUTE">
<%
	final User user = (User) request.getSession(false).getAttribute("USER_SESSION_ATTRIBUTE");
	if (user.hasRole("myorg.domain.RoleType.MANAGER")) {
%>

<style type="text/css">
html { margin-top: 28px !important; }
* html body { margin-top: 28px !important; }
</style>


<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>

<logic:notEmpty name="menuElements">
<nav id="admin-bar">
	<ul class="admin-bar-top">
		<li><span class="nav-title">ADMIN BAR</span></li>
		<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
		<logic:equal name="node" property="manager" value="true">
		<logic:notEmpty name="node" property="children">
		<li class="contains-submenu">
		</logic:notEmpty>
		<logic:empty name="node" property="children">
		<li>
		</logic:empty>
			<% String url = node.getUrl(); %>
			<% if (url.indexOf(':') > 0) { %>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= url %>">
					<logic:present name="node" property="link">
						<span><bean:write name="node" property="link"/></span>
					</logic:present>
					<logic:notPresent name="node" property="link">
						<span><bean:write name="node"/></span>
					</logic:notPresent>
				</html:link>
			<% } else { %>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= url %>">
					<logic:present name="node" property="link">
						<span><bean:write name="node" property="link"/></span>
					</logic:present>
					<logic:notPresent name="node" property="link">
						<span><bean:write name="node"/></span>
					</logic:notPresent>					
				</html:link>				
			<% } %>
			<logic:notEmpty name="node" property="children">
			<ul class="admin-bar-sub">
				<logic:iterate id="subnode" name="node" property="children" type="myorg.domain.contents.Node">
					<logic:equal name="subnode" property="manager" value="true">
					<li>
						<% url = subnode.getUrl(); %>
						<% if (url.indexOf(':') > 0) { %>
							<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(subnode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= url %>">
								<logic:present name="subnode" property="link">
									<span><bean:write name="subnode" property="link"/></span>
								</logic:present>
								<logic:notPresent name="subnode" property="link">
									<span><bean:write name="subnode"/></span>
								</logic:notPresent>
							</html:link>
						<% } else { %>
							<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(subnode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= url %>">
								<logic:present name="subnode" property="link">
									<span><bean:write name="subnode" property="link"/></span>
								</logic:present>
								<logic:notPresent name="subnode" property="link">
									<span><bean:write name="subnode"/></span>
								</logic:notPresent>
							</html:link>				
						<% } %>
					</li>
					</logic:equal>
				</logic:iterate>
			</ul>
			</logic:notEmpty>
		</li>
		</logic:equal>
		</logic:iterate>
	</ul>
</nav>
</logic:notEmpty>

<% } %>
</logic:present>

