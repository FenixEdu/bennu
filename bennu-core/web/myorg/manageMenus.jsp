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
		<fr:property name="param(edit)" value="externalId/virtualHostId" />
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
<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= addContentUrl %>">
	<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.add.feature"/>
</html:link> | 
<bean:define id="createNodeUrl">/configuration.do?method=prepareCreateNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= createNodeUrl %>">
	<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.create.node"/>
</html:link>

<br/>

<div id="navlist">
	<logic:empty name="nodesToManage">
		<br/>
		<bean:message bundle="MYORG_RESOURCES" key="label.virtualHost.no.menu.elements"/>
	</logic:empty>
	<logic:notEmpty name="nodesToManage">


<style type="text/css">
	#menuDrag { list-style-type: none; margin: 0; padding: 0; width: 60%; }
	#menuDrag li { margin: 0 3px 3px 3px; padding: 0.4em; padding-left: 1.5em; height: 18px; }
	#menuDrag li span { position: absolute; margin-left: -1.3em; }
	</style>
	<script type="text/javascript">
	$(function() {
		$("#menuDrag").sortable({ handle: ".ui-icon" });
		$("#menuDrag").disableSelection();
		$("#calculateNewOrder").click(function() {
			var newOrder = "";
			$("#menuDrag > li").each(function(index) {
				if (newOrder.length > 0) {
					newOrder = newOrder + ';' + $(this).attr('id');
				}else {
					newOrder = $(this).attr('id');
				}
				
			});

			$("#articleOrders").attr('value',newOrder);
			$("#orderForm").submit();
			
		});
	});
</script>

	<ul id="menuDrag" style="width: 80%; list-style: none;"> 
		<logic:iterate id="node" name="nodesToManage" type="myorg.domain.contents.Node" indexId="nodeIndex">
			<li id="<%= "articleNode" + nodeIndex %>" class="ui-state-default"><span class="ui-icon ui-icon-arrowthick-2-n-s" style="float: left; cursor: move;"></span>
				<div>
				 
				<div style="float: right;">
				<bean:define id="editNodeUrl">/configuration.do?method=editNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeId=<bean:write name="node" property="externalId"/></bean:define>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= editNodeUrl %>">
					<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.edit" />
				</html:link>
				<bean:define id="selectNodeUrl">/configuration.do?method=manageMenus&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;parentOfNodesToManageId=<bean:write name="node" property="externalId"/></bean:define>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= selectNodeUrl %>">
					<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.select"/>
				</html:link>
				<bean:define id="deleteNodeUrl">/configuration.do?method=deleteNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeToDeleteId=<bean:write name="node" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= deleteNodeUrl %>">
					<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.delete"/>
				</html:link>
				<bean:define id="editNodeUrl">/configuration.do?method=editNodeAvailability&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeId=<bean:write name="node" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
				<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= editNodeUrl %>">
					<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.editAvailability"/>
				</html:link>
				</div>
<bean:write name="node" property="link"/>
				</div>
			</li>
		</logic:iterate>
	</ul>
		
		<form id="orderForm" action="<%= request.getContextPath() %>/content.do" method="post">
			<bean:define id="virtualHostToManageId" name="virtualHostToManage" property="externalId" type="java.lang.String"/>
			<input type="hidden" name="virtualHostToManageId" value="<%= virtualHostToManageId %>"/>
			<logic:present name="parentOfNodesToManage">
				<bean:define id="parentOfNodesToManageId" name="parentOfNodesToManage" property="externalId" type="java.lang.String"/>
				<input type="hidden" name="parentOfNodesToManage" value="<%= parentOfNodesToManageId %>"/>
			</logic:present>
			<input type="hidden" name="method" value="savePageOrders"/>
			<input type="hidden" id="articleOrders" name="articleOrders"/>
			<bean:define id="originalArticleIds"><logic:iterate id="node" name="nodesToManage" indexId="nindex"><% if (nindex > 0) {%>;<% } %><bean:write name="node" property="externalId"/></logic:iterate></bean:define>
			<input type="hidden" name="originalArticleIds" value="<%= originalArticleIds %>"/>
			<bean:define id="buttonLabel"><bean:message bundle="MYORG_RESOURCES" key="label.content.page.order.save"/></bean:define>
			<input id="calculateNewOrder" type="button" value="<%= buttonLabel %>"/>
		</form>
				
	</logic:notEmpty>
</div>
