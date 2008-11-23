<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<bean:define id="context" type="myorg.presentationTier.Context" name="_CONTEXT_"/>
<bean:define id="selectedNode" name="context" property="selectedNode"/>

<h2><bean:message key="label.content.page.edit.title" bundle="MYORG_RESOURCES" /></h2>
<fr:edit id="page" name="selectedNode" property="childPage" schema="module.contents.domain.Page" action="content.do?method=viewPage">
	<fr:layout name="tabular">
		<fr:property name="classes" value="form thwidth150px"/>
		<fr:property name="columnClasses" value=",,tderror"/>
	</fr:layout>
</fr:edit>
