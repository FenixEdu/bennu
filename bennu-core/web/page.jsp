<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<logic:present name="selectedNode">
	<bean:define id="selectedPage" name="selectedNode" property="childPage"/>
	<h2><bean:write name="selectedPage" property="title"/></h2>
	<bean:define id="confirmDelete">return confirmDelete('<bean:message bundle="MYORG_RESOURCES" key="label.content.section.delete.confirm"/>');</bean:define>
	<div id="mainContainer">
		<div id="dragableElementsParentBox">
			<logic:iterate id="section" name="selectedPage" property="orderedSections" indexId="sindex">
				<bean:define id="articleId">article<%= sindex %></bean:define>
				<div dragableBox="true" id="<%= sindex %>">
					<div id="contentStructuredP">
						<logic:present name="section" property="title">
							<logic:notEmpty name="section" property="title">
								<div style="float: right;">
									<html:link page="/content.do?method=prepareEditSection" paramId="sectionOid" paramName="section" paramProperty="OID">
										<bean:message bundle="MYORG_RESOURCES" key="label.content.section.edit"/>
									</html:link>
									|
									<html:link page="/content.do?method=deleteSection" paramId="sectionOid" paramName="section" paramProperty="OID"
											onclick="<%= confirmDelete %>">
										<bean:message bundle="MYORG_RESOURCES" key="label.content.section.delete"/>
									</html:link>
								</div>
								<h3>
									<bean:write name="section" property="title" filter="false"/>
								</h3>
							</logic:notEmpty>
						</logic:present>
						<bean:write name="section" property="contents" filter="false"/>
					</div>
				</div>
			</logic:iterate>
			<div class="clear" id="clear"></div>
		</div>
		<logic:notEmpty name="selectedPage" property="sections">
			<form action="<%= request.getContextPath() %>/content.do" method="post">
				<input type="hidden" name="method" value="saveSectionOrders">
				<input type="hidden" id="sectionOrders" name="sectionOrders">
				<bean:define id="nodeOid" name="selectedNode" property="OID"/>
				<input type="hidden" name="nodeOid" value="<%= nodeOid %>">
				<bean:define id="originalSectionIds"><logic:iterate id="section" name="selectedPage" property="orderedSections" indexId="sindex"><% if (sindex > 0) {%>;<% } %><bean:write name="section" property="OID"/></logic:iterate></bean:define>
				<input type="hidden" name="originalSectionIds" value="<%= originalSectionIds %>">
				<bean:define id="buttonLabel"><bean:message bundle="MYORG_RESOURCES" key="label.content.section.order.save"/></bean:define>
				<input type="submit" value="<%= buttonLabel %>" onclick="saveSectionOrders();">
			</form>
		</logic:notEmpty>
	</div>
	<div id="insertionMarker">
		<img src="<%= request.getContextPath() %>/CSS/marker_top.gif">
		<img src="<%= request.getContextPath() %>/CSS/marker_middle.gif" id="insertionMarkerLine">
		<img src="<%= request.getContextPath() %>/CSS/marker_bottom.gif">
	</div>
</logic:present>
<logic:notPresent name="selectedNode">
	<h2>Welcome</h2>
	<p>
		This application has not yet been configured. To do so please log in with a user that has management priveledges, and start creating contents.
		Remember that you cad define which users have management roles in your build.properties file.
	</p>
</logic:notPresent>
