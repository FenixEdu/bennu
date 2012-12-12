<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>

<bean:define id="context" type="pt.ist.bennu.core.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>

<logic:notEmpty name="menuElements">
	<logic:iterate id="node" name="menuElements" indexId="nindex" type="pt.ist.bennu.core.domain.contents.Node">
		<logic:equal name="node" property="accessible" value="true">
			<logic:notEmpty name="node" property="children">
				<% if (context.contains(node)) { %>
					<% boolean isFirst = true; %>
					<ul>
						<logic:iterate id="childNode" name="node" property="orderedChildren" type="pt.ist.bennu.core.domain.contents.Node">
							<logic:equal name="childNode" property="accessible" value="true">
								<% if (isFirst) {
							    	isFirst = false;
								} else { %>
									<span class="bar">|</span>
								<% } %>
								<li class="navsublist">
									<% if (childNode.getUrl().indexOf(':') > 0) { %>
										<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(childNode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link href="<%= childNode.getUrl() %>">
											<span><bean:write name="childNode" property="link" /></span>
										</html:link>
									<% } else { %>
										<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><% if(childNode.hasFunctionality()) { %><!-- NO_CHECKSUM --><% } %><html:link page="<%= childNode.getUrl() %>">
											<span><bean:write name="childNode" property="link" /></span>
										</html:link>
									<% } %>
								</li>
							</logic:equal>
						</logic:iterate>
					</ul>
				<% } %>
			</logic:notEmpty>
		</logic:equal>
	</logic:iterate>
</logic:notEmpty>
