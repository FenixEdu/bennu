<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration.basic"/>
</h2>

<logic:present name="virtualHostToConfigure">
	
	<bean:define id="vhostOID" name="virtualHost" property="OID"/>
	
	<fr:form action='<%= "/configuration.do?method=editBasicApplicationConfiguration&virtualHostId=" + vhostOID%>'>
		<fr:edit id="virtualHostToConfigure" name="virtualHostToConfigure" visible="false"/>
	
		<fr:edit id="configuration" name="virtualHostToConfigure" schema="virtualHost.application.configuration">
			<fr:layout name="tabular">
				<fr:property name="classes" value="form thwidth150px"/>
				<fr:property name="columnClasses" value=",,tderror"/>
			</fr:layout>
		</fr:edit>
		
		
		<table>
			<tr>
				<td><bean:message key="label.googleSearchEnabled" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="googleSearchEnabled"/></td>
			</tr>
			<tr>
				<td><bean:message key="label.languageSelectionEnabled" bundle="MYORG_RESOURCES"/></td>
				<td><fr:edit name="virtualHostToConfigure" slot="languageSelectionEnabled"/></td>
			</tr>
		</table>
		
		
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

	<br/>

	<bean:define id="themeName" name="virtualHostToConfigure" property="theme.name"/>
	<iframe src="<%= request.getContextPath() + "/home.do?method=firstPage&themeName=" + themeName %>"
			width="90%" height="400" frameborder="0">
		<p>Preview not available.</p>
	</iframe>

</logic:present>
