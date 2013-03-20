<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<fr:edit name="node">
	<fr:schema type="pt.ist.bennu.core.domain.contents.Node" bundle="MYORG_RESOURCES">
		<fr:slot name="accessibilityGroup" layout="menu-select">
			<fr:property name="providerClass" value="pt.ist.bennu.core.presentationTier.renderers.providers.AccessibilityGroupsProvider"/>
			<fr:property name="format" value="${name}"/>
			 
		</fr:slot>
	</fr:schema>
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2"/>
	</fr:layout>
</fr:edit>
