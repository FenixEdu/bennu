<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<bean:define id="context" type="pt.ist.bennu.core.presentationTier.Context" name="_CONTEXT_"/>

<logic:equal name="virtualHost" property="breadCrumbsEnabled" value="true">
	<bean:size id="ne" name="context" property="elements"/>
	<logic:greaterThan name="ne" value="1">
		<div id="beadCrumbs">
			<logic:iterate id="node" type="pt.ist.bennu.core.domain.contents.Node" name="context" property="elements" indexId="i">
				<logic:greaterThan name="i" value="0">
					>
				</logic:greaterThan>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= node.getUrl() %>">
					<bean:write name="node" property="link"/>
				</html:link>
			</logic:iterate>
		</div>
	</logic:greaterThan>
</logic:equal>
