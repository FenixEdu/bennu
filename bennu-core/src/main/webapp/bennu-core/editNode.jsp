<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>
<%@ page import="pt.ist.bennu.core.domain.contents.ActionNode"%>

<bean:define id="manageMenus">/configuration.do?method=manageMenus&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/></bean:define>
<bean:define id="switchEditMode">/configuration.do?method=editNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeId=<bean:write name="node" property="externalId"/>&amp;editSemantic=<bean:write name="editSemantic"/></bean:define>
<bean:define id="editSemantic" name="editSemantic" />

<p>
<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= manageMenus %>">
	<< <bean:message bundle="MYORG_RESOURCES" key="link.back"/>
</html:link> | 
<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= switchEditMode %>">
	<bean:message bundle="MYORG_RESOURCES" key="<%= "label.configuration.node.edit.semantic." + editSemantic %>"/>
</html:link>
</p>

<h2>
	<bean:message key="label.configuration.node.edit" bundle="MYORG_RESOURCES"/>
</h2>

<bean:define id="nodeClass" name="node" property="class"/>
<h3>
	<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.type"/>: <bean:write name="nodeClass" property="name" />
</h3>

<logic:equal name="editSemantic" value="false">
	<bean:define id="nodeEdit" name="node"/>
	
	<fr:edit name="nodeEdit" schema="<%="pt.ist.bennu.core.domain.contents.edit." + nodeEdit.getClass().getSimpleName() %>" >
		<fr:layout name="tabular">
			<fr:property name="classes" value="tstyle2"/>
		</fr:layout>
	</fr:edit>
	
</logic:equal>

<logic:equal name="editSemantic" value="true">
	<logic:equal name="node" property="acceptsFunctionality" value="false" >
		<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.no.semanticLink" />
	</logic:equal>
	
	<logic:equal name="node" property="acceptsFunctionality" value="true" >
		<h3><bean:message key="label.configuration.node" bundle="MYORG_RESOURCES"/>: <bean:write name="node" property="link"/></h3>
		<h3><bean:message key="label.configuration.node.semanticLink" bundle="MYORG_RESOURCES"/>: 
		<%
			ActionNode node = (ActionNode)request.getAttribute("node");
			String nodePath = node.getPath();
			String nodeMethod = node.getMethod();
			if (pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter.hasSemanticURL(nodePath, nodeMethod)) { %>
				<bean:define id="semanticLink" value="<%= pt.ist.bennu.core.presentationTier.servlets.filters.FunctionalityFilter.getSemanticURL(nodePath, nodeMethod) %>" />
				<bean:write name="semanticLink" />
		<% } else { %>
				<bean:message key="label.configuration.node.semanticLink.missing" bundle="MYORG_RESOURCES" />
		<% } %>
		</h3>
		
		<bean:define id="manageMenus">/configuration.do?method=manageMenus&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/></bean:define>
		
		<bean:define id="editPostBack">/configuration.do?method=editNode&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/>&amp;nodeId=<bean:write name="node" property="externalId"/></bean:define>
			
		<fr:form action="<%= editPostBack %>">
			<fr:edit name="node">
				<fr:schema type="pt.ist.bennu.core.domain.contents.Node" bundle="MYORG_RESOURCES">
					<fr:slot name="path" layout="menu-select-postback">
			     		<fr:property name="providerClass" value="pt.ist.bennu.core.presentationTier.renderers.providers.FunctionalitiesPathProvider" />
			     		<fr:property name="destination" value="postback" />
					</fr:slot>
					<fr:slot name="method" layout="menu-select-postback">
			     		<fr:property name="providerClass" value="pt.ist.bennu.core.presentationTier.renderers.providers.FunctionalitiesMethodProvider" />
			     		<fr:property name="destination" value="postback" />
					</fr:slot>
				</fr:schema>
				<fr:layout name="tabular">
					<fr:property name="classes" value="tstyle2"/>
				</fr:layout>
				<fr:destination name="postback" path="<%= editPostBack %>"/>
			</fr:edit>
		</fr:form>
		
		<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="<%= manageMenus %>">
			<bean:message bundle="MYORG_RESOURCES" key="label.configuration.node.functionality.save"/>
		</html:link>
	</logic:equal>
</logic:equal>
