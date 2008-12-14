<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.content.new" bundle="MYORG_RESOURCES"/></h2>

<logic:empty name="contentCreators">
	<bean:message key="label.content.no.new.content.available" bundle="MYORG_RESOURCES"/>
</logic:empty>

<logic:notEmpty name="contentCreators">
	<logic:iterate id="contentCreator" name="contentCreators">
		<bean:define id="path" name="contentCreator" property="path" type="java.lang.String"/>
		<bean:define id="bundle" name="contentCreator" property="bundle" type="java.lang.String"/>
		<bean:define id="key" name="contentCreator" property="key" type="java.lang.String"/>
		<html:link action="<%= path %>">
			<bean:message bundle="<%= bundle %>" key="<%= key %>"/>
		</html:link>
		<br/>
	</logic:iterate>
</logic:notEmpty>
