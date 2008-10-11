<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<div id="pageops">
	<html:link page="/content.do?method=prepareCreateNewPage">
		New
	</html:link>
	<logic:present name="selectedNode">
		|
		<html:link page="/content.do?method=prepareEditPage" paramId="nodeOid" paramName="selectedNode" paramProperty="OID">
			Edit
		</html:link>
		|
		<html:link page="/content.do?method=deletePage" paramId="nodeOid" paramName="selectedNode" paramProperty="OID">
			Delete
		</html:link>
	</logic:present>
</div>
<br/>
