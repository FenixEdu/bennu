<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.utl.ist.fenix.tools.util.i18n.Language"%>


<bean:define id="virtualHost" name="virtualHost" type="myorg.domain.VirtualHost"/>

<logic:equal name="virtualHost" property="languageSelectionEnabled" value="true">
	<bean:define id="languageUrl"><%= request.getContextPath() %>/home.do</bean:define>
	<logic:equal name="virtualHost" property="languageSelectionAsMenu" value="true">
		<form action="<%= languageUrl %>" method="post">
			<input type="hidden" name="method" value="firstPage" />
			<logic:present name="selectedNode">
				<bean:define id="arg" name="selectedNode" property="externalId" type="java.lang.String"/>
				<input type="hidden" name="nodeOid" value="<%= arg %>"/>
			</logic:present>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<select name="locale" onchange="this.form.submit();">
				<% final String country = Language.getLocale().getCountry(); %>
				<% for (Language language : Language.values()) {
				    if (virtualHost.supports(language)) {
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
					}
				} %>
			</select>
		</form>
	</logic:equal>
	<logic:notEqual name="virtualHost" property="languageSelectionAsMenu" value="true">
		<% final String country = Language.getLocale().getCountry(); %>
		<% for (Language language : Language.values()) {
				if (virtualHost.supports(language)) {
					if (language == Language.getLanguage()) {
		%>
						<%= language.name().toUpperCase() %>
		<%
					} else {
		%>
						<html:link action="<%= "/home.do" + "?method=firstPage&locale=" + language.name() + "_" + country %>">
							<%= language.name().toUpperCase() %>
						</html:link>
		<%
					}
				}
			}
		%>

	</logic:notEqual>
</logic:equal>
