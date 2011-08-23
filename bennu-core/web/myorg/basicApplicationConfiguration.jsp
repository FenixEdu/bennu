<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic"/>
</h2>

<logic:present name="virtualHostToConfigure">
	
	<bean:define id="vhostOID" name="virtualHost" property="externalId" type="java.lang.String"/>

	<br/>
	&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;
	<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=themeApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.theme.and.logos"/>
	</html:link>
	&nbsp;&nbsp;&nbsp;
	<!-- HAS_CONTEXT --><html:link page="/configuration.do?method=languageApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.language"/>
	</html:link>

	<fr:form action='<%= "/configuration.do?method=editBasicApplicationConfiguration&virtualHostId=" + vhostOID%>'>
		<fr:edit id="virtualHostToConfigure" name="virtualHostToConfigure" visible="false"/>

		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic.headers.and.footers"/>
		</h3>
	
		<fr:edit id="configuration" name="virtualHostToConfigure" schema="virtualHost.application.configuration.headers.and.footers">
			<fr:layout name="tabular">
				<fr:property name="classes" value="form thwidth150px"/>
				<fr:property name="columnClasses" value=",,tderror"/>
			</fr:layout>
		</fr:edit>

		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="virtualHost.application.configuration.external.interactions"/>
		</h3>
	
		<fr:edit id="configuration" name="virtualHostToConfigure" schema="virtualHost.application.configuration.external.interactions">
			<fr:layout name="tabular">
				<fr:property name="classes" value="form thwidth150px"/>
				<fr:property name="columnClasses" value=",,tderror"/>
			</fr:layout>
		</fr:edit>

		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="virtualHost.application.configuration.content.and.navigation"/>
		</h3>
	
		<table>
			<tr>
				<td><bean:message key="label.googleSearchEnabled" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="googleSearchEnabled"/></td>
			</tr>
			<tr>
				<td><bean:message key="label.languageSelectionEnabled" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="languageSelectionEnabled"/></td>
			</tr>
			<tr>
				<td><bean:message key="label.application.breadCrumbs" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="breadCrumbsEnabled"/></td>
			</tr>
		</table>
		
		
	<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit>
	</fr:form>

</logic:present>
