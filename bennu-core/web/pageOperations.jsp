<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<logic:present name="USER_SESSION_ATTRIBUTE">
	<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>

	<div id="pageops">
		<logic:present name="context" property="selectedNode">
			<html:link page="/content.do?method=prepareCreateNewPage">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.page.new"/>
			</html:link>
			<bean:define id="selectedNode" name="context" property="selectedNode"/>
			<bean:define id="contextPath" name="context" property="path"/>
			|
			<html:link page="/content.do?method=prepareEditPage" paramId="_CONTEXT_PATH_" paramName="contextPath">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.page.edit"/>
			</html:link>
			|
			<html:link page="/content.do?method=reorderPages" paramId="_CONTEXT_PATH_" paramName="contextPath">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.page.order.change"/>
			</html:link>
			|
			<bean:define id="confirmDelete">return confirmDelete('<bean:message bundle="MYORG_RESOURCES" key="label.content.page.delete.confirm"/>');</bean:define>
			<html:link page="/content.do?method=deletePage" paramId="_CONTEXT_PATH_" paramName="contextPath" onclick="<%= confirmDelete %>">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.page.delete"/>
			</html:link>
			|
			<html:link page="/content.do?method=prepareAddSection" paramId="_CONTEXT_PATH_" paramName="contextPath">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.section.add"/>
			</html:link>
		</logic:present>
	</div>
<br/>
<br/>
</logic:present>