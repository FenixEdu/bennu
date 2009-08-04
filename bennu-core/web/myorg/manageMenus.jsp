<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2>
	<bean:message key="label.configuration.manage.menus.features" bundle="MYORG_RESOURCES"/>
</h2>

<fr:view name="virtualHostToManage" schema="virtualHost.application.configuration.summary">
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2" />

		<fr:property name="link(edit)" value="/configuration.do?method=basicApplicationConfiguration" />
		<fr:property name="key(edit)" value="label.edit" />
		<fr:property name="param(edit)" value="OID/virtualHostId" />
		<fr:property name="bundle(edit)" value="MYORG_RESOURCES" />
	</fr:layout>
</fr:view>

<br/>

<logic:present name="parentOfNodesToManage">
	<h3>
		<bean:write name="parentOfNodesToManage" property="link"/>
	</h3>
</logic:present>

<bean:define id="addContentUrl">/home.do?method=addContent&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
<!-- HAS_CONTEXT --><html:link page="<%= addContentUrl %>">
	<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.add.feature"/>
</html:link>

<br/>

<script type="text/javascript" src="<%= request.getContextPath() + "/javaScript/dragContents.js" %>"></script>

<div id="navlist">
	<logic:empty name="nodesToManage">
		<br/>
		<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.no.menu.elements"/>
	</logic:empty>
	<logic:notEmpty name="nodesToManage">

		<table id="dragableElementsParentBox" class="tstyle2">
			<logic:iterate id="node" name="nodesToManage" indexId="nindex" type="myorg.domain.contents.Node">
				<bean:define id="articleId">articleNode<%= nindex %></bean:define>
				<tr dragableBox="true" id="<%= articleId %>">
					<th>
						<bean:write name="node" property="link"/>
					</th>
					<td>
						<bean:define id="selectNodeUrl">/configuration.do?method=manageMenus&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;parentOfNodesToManageId=<bean:write name="node" property="externalId"/></bean:define>
						<!-- HAS_CONTEXT --><html:link page="<%= selectNodeUrl %>">
							<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.select"/>
						</html:link>
					</td>
					<td>
						<bean:define id="deleteNodeUrl">/configuration.do?method=deleteNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeToDeleteId=<bean:write name="node" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
						<!-- HAS_CONTEXT --><html:link page="<%= deleteNodeUrl %>">
							<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.delete"/>
						</html:link>
					</td>
				</tr>
			</logic:iterate>
		</table>
					<form action="<%= request.getContextPath() %>/content.do" method="post">
						<bean:define id="virtualHostToManageId" name="virtualHostToManage" property="externalId"/>
						<input type="hidden" name="virtualHostToManageId" value="<%= virtualHostToManageId %>"/>
						<logic:present name="parentOfNodesToManage">
							<bean:define id="parentOfNodesToManageId" name="parentOfNodesToManage" property="externalId"/>
							<input type="hidden" name="parentOfNodesToManage" value="<%= parentOfNodesToManageId %>"/>
						</logic:present>
						<input type="hidden" name="method" value="savePageOrders"/>
						<input type="hidden" id="articleOrders" name="articleOrders"/>
						<bean:define id="originalArticleIds"><logic:iterate id="node" name="nodesToManage" indexId="nindex"><% if (nindex > 0) {%>;<% } %><bean:write name="node" property="externalId"/></logic:iterate></bean:define>
						<input type="hidden" name="originalArticleIds" value="<%= originalArticleIds %>"/>
						<bean:define id="buttonLabel"><bean:message bundle="MYORG_RESOURCES" key="label.content.page.order.save"/></bean:define>
						<input type="submit" value="<%= buttonLabel %>" onclick="saveArticleOrders();"/>
					</form>
					<div id="insertionMarker">
						<img src="<%= request.getContextPath() %>/CSS/marker_top.gif" alt=""/>
						<img src="<%= request.getContextPath() %>/CSS/marker_middle.gif" id="insertionMarkerLine" alt=""/>
						<img src="<%= request.getContextPath() %>/CSS/marker_bottom.gif" alt=""/>
					</div>
	</logic:notEmpty>
</div>
