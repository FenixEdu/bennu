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

	<br/>
	&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=basicApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic"/>
	</html:link>
	&nbsp;&nbsp;&nbsp;
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=languageApplicationConfiguration"
			paramId="virtualHostId" paramName="virtualHost" paramProperty="externalId">
		<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.language"/>
	</html:link>

	<form>
		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.logos.and.icons"/>
		</h3>
	</form>

	<table>
		<tr>
			<td>
				<logic:present name="virtualHost" property="logo">
					<html:img action='<%= "/home.do?method=logo&virtualHostId=" + vhostOID%>'/>
				</logic:present>

				<fr:edit id="virtualHostToConfigureLogo" name="virtualHostToConfigure" schema="virtualHost.application.configuration.logo"
						action='<%= "/configuration.do?method=editBasicApplicationConfigurationLogo&virtualHostId=" + vhostOID%>'>
					<fr:layout name="tabular">
						<fr:property name="classes" value="form thwidth150px"/>
						<fr:property name="columnClasses" value=",,tderror"/>
					</fr:layout>
				</fr:edit>
			</td>
			<td>
				<logic:present name="virtualHost" property="favicon">
					<html:img action='<%= "/home.do?method=favico&virtualHostId=" + vhostOID%>'/>
				</logic:present>

				<fr:edit id="virtualHostToConfigureFavico" name="virtualHostToConfigure" schema="virtualHost.application.configuration.favico"
						action='<%= "/configuration.do?method=editBasicApplicationConfigurationFavico&virtualHostId=" + vhostOID%>'>
					<fr:layout name="tabular">
						<fr:property name="classes" value="form thwidth150px"/>
						<fr:property name="columnClasses" value=",,tderror"/>
					</fr:layout>
				</fr:edit>
			</td>
		</tr>
	</table>

	<br/>

	<form>
		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.layout"/>
		</h3>
	</form>

	<fr:form action='<%= "/configuration.do?method=editBasicApplicationConfiguration&virtualHostId=" + vhostOID%>'>
		<fr:edit id="virtualHostToConfigure" name="virtualHostToConfigure" visible="false"/>

		<table>
			<tr>
				<td><bean:message key="label.application.configuration.layout" bundle="MYORG_RESOURCES"/></td>
				<td>
					<fr:edit name="virtualHostToConfigure" slot="layoutSubDir"/>
				</td>
			</tr>
		</table>

		<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit>
	</fr:form>

	<br/>

	<form>
		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.theme"/>
		</h3>
	</form>

	<fr:form action='<%= "/configuration.do?method=editBasicApplicationConfiguration&virtualHostId=" + vhostOID%>'>
		<fr:edit id="virtualHostToConfigure" name="virtualHostToConfigure" visible="false"/>

		<table>
			<tr>
				<td><bean:message key="label.selectTheme" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="theme">
						<fr:layout name="menu-select-postback">
							<fr:property name="providerClass" value="myorg.presentationTier.renderers.providers.ThemeProvider"/>
							<fr:property name="eachLayout" value="values"/>
							<fr:property name="eachSchema" value="theme.name"/>
						</fr:layout>
				
						<fr:destination name="postBack" path='<%= "/configuration.do?method=postbackBasicApplicationConfiguration&virtualHostId=" + vhostOID %>'/>  
					</fr:edit>
				</td>
			</tr>
			<logic:present name="virtualHostToConfigure" property="theme">
				<tr>
					<td colspan="2">
						<fr:view name="virtualHostToConfigure" property="theme.description"/>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<bean:define id="themeName" name="virtualHostToConfigure" property="theme.name"/>
						<bean:define id="screenShot" name="virtualHostToConfigure" property="theme.screenshotFileName"/>

<%--
						<img src='<%= request.getContextPath() + "/CSS/" + themeName + "/" + screenShot %>'/>
--%>
					</td>
				</tr>
			</logic:present>
		</table>
		
	<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit>
	</fr:form>

<%--
	<br/>

	<form>
		<h3>
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.preview"/>
		</h3>
	</form>

	<bean:define id="themeId" name="virtualHostToConfigure" property="theme.externalId" type="java.lang.String"/>
	<iframe src="<%= request.getContextPath() + "/home.do?method=previewTheme&themeId=" + themeId %>"
			width="90%" height="400" frameborder="0">
		<p>Preview not available.</p>
	</iframe>

--%>

</logic:present>
