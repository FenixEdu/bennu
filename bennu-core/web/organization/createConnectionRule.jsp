<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="label.connection.rule" bundle="ORGANIZATION_RESOURCES" /></h2>

<html:messages id="message" message="true" bundle="ORGANIZATION_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>

<bean:define id="accountabilityTypeOid" name="connectionRuleBean" property="accountabilityType.OID" />

<fr:edit id="connectionRuleBean" name="connectionRuleBean"
	schema="organization.ConnectionRuleBean"
	action="/organization.do?method=createConnectionRule">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px" />
		<fr:property name="columnClasses" value=",,tderror" />
	</fr:layout>
	<fr:destination name="cancel" path='<%= "/organization.do?method=viewConnectionRules&accountabilityTypeOid=" + accountabilityTypeOid %>' />
</fr:edit>
