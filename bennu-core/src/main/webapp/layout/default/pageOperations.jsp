<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<logic:present name="USER_SESSION_ATTRIBUTE">
<logic:present role="pt.ist.bennu.core.domain.RoleType.MANAGER">
	<bean:define id="context" type="pt.ist.bennu.core.presentationTier.Context" name="_CONTEXT_"/>

	<div id="pageops">
		<logic:present name="context" property="selectedNode">
			<bean:define id="selectedNode" name="context" property="selectedNode"/>
			<bean:define id="contextPath" name="context" property="path"/>
			<html:link page="/content.do?method=prepareAddSection" paramId="_CONTEXT_PATH_" paramName="contextPath">
				<bean:message bundle="MYORG_RESOURCES" key="label.content.section.add"/>
			</html:link>
		</logic:present>
	</div>
<br/>
<br/>
</logic:present>
</logic:present>
