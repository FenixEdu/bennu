<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<fr:edit name="node">
	<fr:schema type="myorg.domain.contents.Node" bundle="MYORG_RESOURCES">
		<fr:slot name="accessibilityGroup" layout="menu-select">
			<fr:property name="providerClass" value="myorg.presentationTier.renderers.providers.AccessibilityGroupsProvider"/>
			<fr:property name="format" value="${name}"/>
			 
		</fr:slot>
	</fr:schema>
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2"/>
	</fr:layout>
</fr:edit>