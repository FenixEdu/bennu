<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>
<bean:define id="prefixPath" name="context" property="prefixPath"/>

<div id="navlist">
<ul>
	<logic:empty name="menuElements">
		<li><!-- NO_CHECKSUM --><a href="home.do?method=firstPage"><bean:message bundle="MYORG_RESOURCES" key="label.application.home"/></a></li>
	</logic:empty>
	<logic:notEmpty name="menuElements">

		<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
			<% if (node.isAccessible()) { %>
				<bean:define id="articleId">articleNode<%= nindex %></bean:define>
				<li>
					<% String url = node.getUrl(); %>
					<div id="<%= articleId %>">
						<!-- HAS_CONTEXT --><html:link page="<%= url %>">
							<bean:write name="node" property="link"/>
						</html:link>
					</div>
				</li>
				<logic:notEmpty name="node" property="children">
					<% if (context.getSelectedNode() == node) { %>
						<logic:iterate id="childNode" name="node" property="orderedChildren" type="myorg.domain.contents.Node">
							<% if (childNode.isAccessible()) { %>
								<% String childUrl = childNode.getUrl(); %>
								<li class="navsublist">
									<!-- HAS_CONTEXT --><html:link page="<%= childUrl %>">
										<bean:write name="childNode" property="link"/>
									</html:link>
								</li>
							<% } %>
						</logic:iterate>
					<% } %>
				</logic:notEmpty>
			<% } %>
		</logic:iterate>
	</logic:notEmpty>
</ul>
</div>