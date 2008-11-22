<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="label.create.new" bundle="ORGANIZATION_RESOURCES" /></h2>

<html:messages id="message" message="true" bundle="ORGANIZATION_RESOURCES">
	<span class="error0"> <bean:write name="message" /> </span>
	<br />
</html:messages>

<html:form action="/organization.do">
	<html:hidden property="method" value="prepareCreateAccountabilityType" />

	<bean:message key="label.accountability.type" bundle="ORGANIZATION_RESOURCES" />
	<html:select property="accountabilityTypeClassName">
		<html:option value="myorg.domain.organization.AccountabilityType$AccountabilityTypeBean"><bean:message key="label.accountability.type" bundle="ORGANIZATION_RESOURCES" /></html:option>
		<html:option value="myorg.domain.organization.ConnectionRuleAccountabilityType$ConnectionRuleAccountabilityTypeBean"><bean:message key="label.connection.rule.accountability.type" bundle="ORGANIZATION_RESOURCES" /></html:option>
	</html:select>

	<html:submit><bean:message key="label.continue" bundle="ORGANIZATION_RESOURCES" /></html:submit>
	<html:cancel onclick="this.form.method.value='viewAccountabilityTypes';return true;" ><bean:message key="label.back" bundle="ORGANIZATION_RESOURCES" /></html:cancel>
</html:form>
