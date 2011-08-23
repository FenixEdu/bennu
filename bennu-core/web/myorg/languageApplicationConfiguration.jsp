<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>: 
	<bean:write name="virtualHost" property="applicationTitle"/>
</h2>

<logic:present name="virtualHostToConfigure">
	
	<bean:define id="vhostOID" name="virtualHost" property="externalId" type="java.lang.String"/>
	<bean:define id="virtualHost" name="virtualHost" type="myorg.domain.VirtualHost"/>

	<br/>
	&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;
	<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=basicApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic"/>
	</html:link>
	&nbsp;&nbsp;&nbsp;
	<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=themeApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.theme.and.logos"/>
	</html:link>

	<form>
		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.language"/>
		</h3>
	</form>

	<form action="<%= request.getContextPath() + "/configuration.do" %>" method="post">
		<html:hidden property="virtualHostId" value="<%= vhostOID %>"/>
		<html:hidden property="method" value="editLanguageApplicationConfiguration"/>
		<table>
			<%
				for (final pt.utl.ist.fenix.tools.util.i18n.Language language : pt.utl.ist.fenix.tools.util.i18n.Language.values()) {
			%>
					<tr>
						<td>
							<%= language.name() %>
						</td>
						<td>
							<input name="<%= language.name() %>" type="checkbox"
								<% if (virtualHost.supports(language)) {
								%>
									checked="checked"
								<%
									}
								%>
							>
						</td>
					</tr>
			<%
				}
			%>
		</table>

		<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit>
	</form>

</logic:present>
