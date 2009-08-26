<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>


<logic:notEmpty name="menuElements">
	<ul>
	<bean:size name="menuElements" id="size"/>

	<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
		<logic:equal name="node" property="accessible" value="true">
			<% if (context.contains(node)) { %>
				<li class="selected">
			<% } else { %>
				<li>
			<% } %>
				<bean:define id="url" name="node" property="url" type="java.lang.String"/>
				<!-- HAS_CONTEXT --><html:link page="<%= url %>">
					<span><bean:write name="node" property="link"/></span>
					<logic:lessThan name="nindex" value="<%= new Integer(size).toString() %>">
						<div class="lic1"></div>
					</logic:lessThan>					
				</html:link>
			</li>
		</logic:equal>
	</logic:iterate>
	</ul>
	
	<div class="clear"></div>
	
</logic:notEmpty>	
