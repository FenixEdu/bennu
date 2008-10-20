<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<div id="navlist">
<%
	final Object o = request.getAttribute("reorderPages");
	final boolean reorder = o != null && ((Boolean) o).booleanValue();
%>
<ul <% if (reorder) {%> id="dragableElementsParentBox" <% } %>>
	<logic:empty name="menuNodes">
		<li><!-- NO_CHECKSUM --><a href="home.do?method=firstPage"><bean:message bundle="MYORG_RESOURCES" key="label.application.home"/></a></li>
	</logic:empty>
	<logic:notEmpty name="menuNodes">

		<logic:iterate id="node" name="menuNodes" indexId="nindex">
			<bean:define id="articleId">articleNode<%= nindex %></bean:define>
			<li>
				<div <% if (reorder) {%>dragableBox="true"<% } %> id="<%= articleId %>">
					<% if (!reorder) { %>
						<html:link page="/content.do?method=viewPage" paramId="nodeOid" paramName="node" paramProperty="OID">
							<bean:write name="node" property="childPage.link"/>
						</html:link>
					<% } else { %>
						<div class="navigationContentMove">
							<bean:write name="node" property="childPage.link"/>
						</div>
					<% } %>
				</div>
			</li>
		</logic:iterate>
	</logic:notEmpty>
	<% if (reorder) {%>
		<li>
				<br/>
				<div class="clear" id="clear"></div>
				<ul>
				<li>
				<form action="<%= request.getContextPath() %>/content.do" method="post">
					<input type="hidden" name="method" value="savePageOrders"/>
					<input type="hidden" id="articleOrders" name="articleOrders"/>
					<bean:define id="nodeOid" name="selectedNode" property="OID"/>
					<input type="hidden" name="nodeOid" value="<%= nodeOid %>"/>
					<bean:define id="originalArticleIds"><logic:iterate id="node" name="menuNodes" indexId="nindex"><% if (nindex > 0) {%>;<% } %><bean:write name="node" property="OID"/></logic:iterate></bean:define>
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
	<% } %>
	<li>
		<p>
			<strong>A tiny little service announcement.</strong>
			<br/>Put all your little tidbits of information or pictures in this small yet useful little area.
		</p>
	</li>
</ul>

</div>