<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<div id="pageops">
	<html:link page="/content.do?method=prepareCreateNewPage">
		<bean:message bundle="MYORG_RESOURCES" key="label.content.page.new"/>
	</html:link>
	<logic:present name="selectedNode">
		|
		<html:link page="/content.do?method=prepareEditPage" paramId="nodeOid" paramName="selectedNode" paramProperty="OID">
			<bean:message bundle="MYORG_RESOURCES" key="label.content.page.edit"/>
		</html:link>
		|
		<bean:define id="confirmDelete">return confirmDelete('<bean:message bundle="MYORG_RESOURCES" key="label.content.page.delete.confirm"/>');</bean:define>
		<html:link page="/content.do?method=deletePage" paramId="nodeOid" paramName="selectedNode" paramProperty="OID" onclick="<%= confirmDelete %>">
			<bean:message bundle="MYORG_RESOURCES" key="label.content.page.delete"/>
		</html:link>
		|
		<html:link page="/content.do?method=prepareAddSection" paramId="nodeOid" paramName="selectedNode" paramProperty="OID">
			<bean:message bundle="MYORG_RESOURCES" key="label.content.section.add"/>
		</html:link>
	</logic:present>
</div>
<br/>
