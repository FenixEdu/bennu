<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.accountability.types" bundle="ORGANIZATION_RESOURCES" /></h2>

<br/>
<html:link action="/organization.do?method=selectAccountabilityType"><bean:message key="label.create.new" bundle="ORGANIZATION_RESOURCES"/></html:link>

<logic:notEmpty name="accountabilityTypes">
	<fr:view name="accountabilityTypes" schema="organization.AccountabilityType.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="form thwidth150px"/>

			<fr:property name="linkFormat(editAccountabilityType)" value="/organization.do?method=prepareEditAccountabilityType&amp;accountabilityTypeOid=${OID}" />
			<fr:property name="key(editAccountabilityType)" value="label.edit"/>
			<fr:property name="bundle(editAccountabilityType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="order(editAccountabilityType)" value="1"/>

			<fr:property name="linkFormat(deleteAccountabilityType)" value="/organization.do?method=deleteAccountabilityType&amp;accountabilityTypeOid=${OID}" />
			<fr:property name="key(deleteAccountabilityType)" value="label.delete"/>
			<fr:property name="bundle(deleteAccountabilityType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="confirmationKey(deleteAccountabilityType)" value="label.delete.confirmation.message"/>
			<fr:property name="confirmationBundle(deleteAccountabilityType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="order(deleteAccountabilityType)" value="2"/>

			<fr:property name="linkFormat(viewConnectionRule)" value="/organization.do?method=viewConnectionRules&amp;accountabilityTypeOid=${OID}" />
			<fr:property name="key(viewConnectionRule)" value="label.connection.rules"/>
			<fr:property name="bundle(viewConnectionRule)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="order(viewConnectionRule)" value="3"/>
			<fr:property name="visibleIf(viewConnectionRule)" value="withRules"/>
			
			<fr:property name="sortBy" value="type" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<logic:empty name="accountabilityTypes">
	<em><bean:message key="label.no.accountability.types" bundle="ORGANIZATION_RESOURCES" /></em>
</logic:empty>
