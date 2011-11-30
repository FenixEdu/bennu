<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<bean:define id="manageMenus">/configuration.do?method=manageMenus&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/></bean:define>

<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= manageMenus %>">
	<bean:message bundle="MYORG_RESOURCES" key="link.back"/>
</html:link>

<h2>
	<bean:message key="label.virtualHost.create.node" bundle="MYORG_RESOURCES"/>
</h2>

<bean:define id="nodeBean" name="nodeBean" />

<bean:define id="createNode">/configuration.do?method=createNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>

<bean:define id="chooseTypePostBack">/configuration.do?method=chooseTypePostBack&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;<logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>

<fr:form action="<%= createNode %>">
	<logic:notPresent name="nodeTypeToCreate">
		<fr:edit name="nodeBean">
			<fr:schema bundle="MYORG_RESOURCES" type="myorg.domain.contents.NodeBean">
				<fr:slot name="nodeType" layout="menu-select-postback">
		     		<fr:property name="providerClass" value="myorg.presentationTier.renderers.providers.NodeTypeProvider" />
		     		<fr:property name="destination" value="postback" />
				</fr:slot>
			</fr:schema>
			<fr:layout>
				<fr:property name="classes" value="tstyle2"/>
			</fr:layout>
			<fr:destination name="postback" path="<%= chooseTypePostBack %>"/>
		</fr:edit>
	</logic:notPresent>

	<logic:present name="nodeTypeToCreate">
		<bean:define id="nodeTypeToCreate" name="nodeTypeToCreate" type="myorg.domain.contents.NodeType" />
		<fr:edit name="nodeBean" schema="<%= "myorg.domain.contents.create.nodeBean." + nodeTypeToCreate.getName() %>" />
	</logic:present>
	
	<html:submit styleClass="inputbutton"><bean:message key="renderers.form.submit.name" bundle="RENDERER_RESOURCES"/></html:submit>
	<html:cancel styleClass="inputbutton"><bean:message key="renderers.form.cancel.name" bundle="RENDERER_RESOURCES"/></html:cancel>
</fr:form>