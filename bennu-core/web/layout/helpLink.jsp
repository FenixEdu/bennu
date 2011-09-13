<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<logic:present name="virtualHost" property="helpLink">
		<logic:notEmpty name="virtualHost" property="helpLink">
			<bean:define id="helpUrl"><bean:write name="virtualHost" property="helpLink"/></bean:define>
			<a href="<%= helpUrl %>" target="_blank"><bean:message key="label.help.link" bundle="MYORG_RESOURCES"/></a> |  
		</logic:notEmpty>
	</logic:present>
</logic:present>