<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<logic:present name="_CONTEXT_" property="selectedNode">
	<ul>
		<logic:iterate id="childNode" name="_CONTEXT_" property="selectedNode.children">
			<logic:equal name="childNode" property="accessible" value="true">
				<bean:define id="childUrl" name="childNode" property="url" type="java.lang.String"/>
				<li class="navsublist"><!-- HAS_CONTEXT --><html:link
					page="<%=childUrl%>">
					<span><bean:write name="childNode" property="link" /></span>
				</html:link></li>
			</logic:equal>

		</logic:iterate>
	</ul>
</logic:present>
