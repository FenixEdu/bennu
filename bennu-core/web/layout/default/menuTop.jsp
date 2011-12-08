<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>


<logic:notEmpty name="menuElements">
	<ul class="mainnav">
	<bean:size name="menuElements" id="size"/>

	<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
		<logic:equal name="node" property="accessible" value="true">
			<% if (context.contains(node)) { %>
				<li class="selected">
			<% } else { %>
				<li>
			<% } %>
					<% final String url = node.getUrl(); %>
					<% if (url.indexOf(':') > 0) { %>
						<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= url %>">
							<logic:present name="node" property="link">
								<span><bean:write name="node" property="link"/></span>
							</logic:present>
							<logic:notPresent name="node" property="link">
								<span><bean:write name="node"/></span>
							</logic:notPresent>
						</html:link>
					<% } else { %>
						<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(node.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= url %>">
							<logic:present name="node" property="link">
								<span><bean:write name="node" property="link"/></span>
							</logic:present>
							<logic:notPresent name="node" property="link">
								<span><bean:write name="node"/></span>
							</logic:notPresent>					
						</html:link>				
					<% } %>
			</li>
		</logic:equal>
	</logic:iterate>
	</ul>
	
	<div class="clear"></div>
	
</logic:notEmpty>	
