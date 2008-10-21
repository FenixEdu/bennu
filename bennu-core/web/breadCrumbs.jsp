<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<% String path = "/content.do?method=viewPage&amp;_CONTEXT_PATH_="; %>

<bean:size id="ne" name="context" property="elements"/>
<logic:greaterThan name="ne" value="1">
	<div id="beadCrumbs">
		<logic:iterate id="node" type="myorg.domain.content.Node" name="context" property="elements" indexId="i">
			<logic:greaterThan name="i" value="0">
				<% path += ","; %>
				>
			</logic:greaterThan>
			<% path += node.getOID(); %>
			<!-- HAS_CONTEXT --><html:link page="<%= path %>">
				<bean:write name="node" property="childPage.link"/>
			</html:link>
		</logic:iterate>
	</div>
	<br/>
</logic:greaterThan>