<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<h2><bean:message key="label.party.types" bundle="ORGANIZATION_RESOURCES" /></h2>

<br/>
<html:link action="/organization.do?method=prepareCreatePartyType"><bean:message key="label.create.new" bundle="ORGANIZATION_RESOURCES"/></html:link>

<logic:notEmpty name="partyTypes">
	<fr:view name="partyTypes" schema="organization.PartyType.view">
		<fr:layout name="tabular">
			<fr:property name="classes" value="form thwidth150px"/>

			<fr:property name="linkFormat(editPartyType)" value="/organization.do?method=prepareEditPartyType&amp;partyTypeOid=${OID}" />
			<fr:property name="key(editPartyType)" value="label.edit"/>
			<fr:property name="bundle(editPartyType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="order(editPartyType)" value="1"/>

			<fr:property name="linkFormat(deletePartyType)" value="/organization.do?method=deletePartyType&amp;partyTypeOid=${OID}" />
			<fr:property name="key(deletePartyType)" value="label.delete"/>
			<fr:property name="bundle(deletePartyType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="confirmationKey(deletePartyType)" value="label.delete.confirmation.message"/>
			<fr:property name="confirmationBundle(deletePartyType)" value="ORGANIZATION_RESOURCES"/>
			<fr:property name="order(deletePartyType)" value="2"/>
			
			<fr:property name="sortBy" value="type" />
		</fr:layout>
	</fr:view>
</logic:notEmpty>

<logic:empty name="partyTypes">
	<em><bean:message key="label.no.party.types" bundle="ORGANIZATION_RESOURCES" /></em>
</logic:empty>