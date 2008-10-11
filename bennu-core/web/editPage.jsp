<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.content.page.edit.title" bundle="MYORG_RESOURCES" /></h2>
<bean:define id="editUrl">content.do?method=viewPage&nodeOid=<bean:write name="selectedNode" property="OID"/></bean:define>
<fr:edit id="page" name="selectedNode" property="childPage" schema="myorg.domain.content.Page" action="<%= editUrl %>">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>
