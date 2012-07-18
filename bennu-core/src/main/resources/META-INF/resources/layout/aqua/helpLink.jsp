<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<logic:present name="virtualHost" property="helpLink">
		<logic:notEmpty name="virtualHost" property="helpLink">
			<bean:define id="helpUrl"><bean:write name="virtualHost" property="helpLink"/></bean:define>
			<a href="<%= helpUrl %>" target="_blank"><bean:message key="label.help.link" bundle="MYORG_RESOURCES"/></a>  
		</logic:notEmpty>
	</logic:present>
</logic:present>
