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

<bean:define id="addContentUrl">/home.do?method=addContent&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="OID"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="OID"/></logic:present></bean:define>
<!-- HAS_CONTEXT --><html:link page="<%= addContentUrl %>">
	<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.add.feature"/>
</html:link>

<br/>
<br/>

<div id="navlist">
	<logic:empty name="nodesToManage">
		<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.no.menu.elements"/>
	</logic:empty>
	<logic:notEmpty name="nodesToManage">

		<ul id="dragableElementsParentBox">
			<logic:iterate id="node" name="nodesToManage" indexId="nindex" type="myorg.domain.contents.Node">
				<bean:define id="articleId">articleNode<%= nindex %></bean:define>
				<li>
					<div dragableBox="true" id="<%= articleId %>">
						<div class="navigationContentMove">
							<bean:write name="node" property="link"/>
						</div>
					</div>
				</li>
			</logic:iterate>
			<li>
				<br/>
				<div class="clear" id="clear"></div>
				<ul>
					<li>
						<form action="<%= request.getContextPath() %>/content.do" method="post">
							<bean:define id="virtualHostToManageId" name="virtualHostToManage" property="OID"/>
							<input type="hidden" name="virtualHostToManageId" value="<%= virtualHostToManageId %>"/>
							<logic:present name="parentOfNodesToManage">
								<bean:define id="parentOfNodesToManageId" name="parentOfNodesToManage" property="OID"/>
								<input type="hidden" name="parentOfNodesToManage" value="<%= parentOfNodesToManageId %>"/>
							</logic:present>
							<input type="hidden" name="method" value="savePageOrders"/>
							<input type="hidden" id="articleOrders" name="articleOrders"/>
							<bean:define id="originalArticleIds"><logic:iterate id="node" name="nodesToManage" indexId="nindex"><% if (nindex > 0) {%>;<% } %><bean:write name="node" property="OID"/></logic:iterate></bean:define>
							<input type="hidden" name="originalArticleIds" value="<%= originalArticleIds %>"/>
							<bean:define id="buttonLabel"><bean:message bundle="MYORG_RESOURCES" key="label.content.page.order.save"/></bean:define>
							<input type="submit" value="<%= buttonLabel %>" onclick="saveArticleOrders();"/>
						</form>
					</li>
				</ul>
				<div id="insertionMarker">
					<img src="<%= request.getContextPath() %>/CSS/marker_top.gif" alt=""/>
					<img src="<%= request.getContextPath() %>/CSS/marker_middle.gif" id="insertionMarkerLine" alt=""/>
					<img src="<%= request.getContextPath() %>/CSS/marker_bottom.gif" alt=""/>
				</div>
			</li>
		</ul>
	</logic:notEmpty>
</div>
