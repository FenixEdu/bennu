<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.tasks.scheduleing"/>
</h2>

<logic:empty name="taskDomainClass">
	<bean:message bundle="MYORG_RESOURCES" key="label.scheduler.tasks.none"/>
</logic:empty>
<logic:notEmpty name="taskDomainClass">
	<logic:iterate id="taskDomainClass" name="taskDomainClasses">
		<bean:write name="taskDomainClass"/>
	</logic:iterate>
</logic:notEmpty>
