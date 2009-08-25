<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="menuElements" name="context" property="menuElements"/>

<logic:notEmpty name="menuElements">
	<logic:iterate id="node" name="menuElements" indexId="nindex" type="myorg.domain.contents.Node">
		<logic:equal name="node" property="accessible" value="true">
			<logic:notEmpty name="node" property="children">
				<% if (context.contains(node)) { %>
					<% boolean isFirst = true; %>
					<ul>
						<logic:iterate id="childNode" name="node" property="orderedChildren" type="myorg.domain.contents.Node">
							<logic:equal name="childNode" property="accessible" value="true">
								<% if (isFirst) {
							    	isFirst = false;
								} else { %>
									<span class="bar">|</span>
								<% } %>
								<li class="navsublist">
									<!-- HAS_CONTEXT --><html:link page="<%= childNode.getUrl() %>">
										<span><bean:write name="childNode" property="link" /></span>
									</html:link>
								</li>
							</logic:equal>
						</logic:iterate>
					</ul>
				<% } %>
			</logic:notEmpty>
		</logic:equal>
	</logic:iterate>
</logic:notEmpty>
