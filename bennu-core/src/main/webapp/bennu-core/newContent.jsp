<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<h2>
	<bean:message key="label.content.new" bundle="MYORG_RESOURCES"/>
</h2>

<logic:empty name="contentCreatorsMap">
	<bean:message key="label.content.no.new.content.available" bundle="MYORG_RESOURCES"/>
</logic:empty>

<logic:notEmpty name="contentCreatorsMap">
	<logic:iterate id="entry" name="contentCreatorsMap">
		<bean:define id="groupKey" name="entry" property="key" type="java.lang.String"/>
		<bean:define id="contentCreators" name="entry" property="value"/>

		<logic:iterate id="contentCreator" name="contentCreators" length="1">
			<h3>
			<bean:define id="bundle" name="contentCreator" property="bundle" type="java.lang.String"/>
			<bean:message bundle="<%= bundle %>" key="<%= groupKey %>"/>
			</h3>
		</logic:iterate>

		<p>
			<logic:iterate id="contentCreator" name="contentCreators">
				<bean:define id="path" name="contentCreator" property="path" type="java.lang.String"/>
				<bean:define id="bundle" name="contentCreator" property="bundle" type="java.lang.String"/>
				<bean:define id="key" name="contentCreator" property="key" type="java.lang.String"/>
				<bean:define id="url"><bean:write name="path"/>&amp;virtualHostToManageId=<bean:write name="virtualHostToManage" property="externalId"/><logic:present name="parentOfNodesToManage">&amp;parentOfNodesToManageId=<bean:write name="parentOfNodesToManage" property="externalId"/></logic:present></bean:define>
				<html:link action="<%= url %>">
					<bean:message bundle="<%= bundle %>" key="<%= key %>"/>
				</html:link>
				<br/>
			</logic:iterate>
		</p>
	</logic:iterate>
</logic:notEmpty>
