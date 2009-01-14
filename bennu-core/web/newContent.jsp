<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.content.new" bundle="MYORG_RESOURCES"/></h2>

<logic:empty name="contentCreatorsMap">
	<bean:message key="label.content.no.new.content.available" bundle="MYORG_RESOURCES"/>
</logic:empty>

<logic:notEmpty name="contentCreatorsMap">
	<logic:iterate id="entry" name="contentCreatorsMap">
		<bean:define id="groupKey" name="entry" property="key" type="java.lang.String"/>
		<bean:define id="contentCreators" name="entry" property="value"/>

		<logic:iterate id="contentCreator" name="contentCreators" length="1">
			<h3>
			<bean:define id="bundle" name="contentCreator" property="bundle" type="java.lang.String"/>
			<bean:message bundle="<%= bundle %>" key="<%= groupKey %>"/>
			</h3>
		</logic:iterate>

		<p>
			<logic:iterate id="contentCreator" name="contentCreators">
				<bean:define id="path" name="contentCreator" property="path" type="java.lang.String"/>
				<bean:define id="bundle" name="contentCreator" property="bundle" type="java.lang.String"/>
				<bean:define id="key" name="contentCreator" property="key" type="java.lang.String"/>
				<html:link action="<%= path %>">
					<bean:message bundle="<%= bundle %>" key="<%= key %>"/>
				</html:link>
				<br/>
			</logic:iterate>
		</p>
	</logic:iterate>
</logic:notEmpty>
