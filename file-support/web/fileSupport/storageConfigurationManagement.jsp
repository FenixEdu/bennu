<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>

<h2><bean:message key="title.storageConfigurationManagement" bundle="FILE_SUPPORT_RESOURCES"/></h2>

<fr:edit schema="edit.fileStorageConfigurations" name="storageConfigurations" >
	<fr:layout name="tabular-editable">
		<fr:property name="classes" value="tstyle2"/>
		<fr:property name="columnClasses" value="smalltxt aleft, smalltxt"/>
		<fr:property name="sortBy" value="fileType"/>
	</fr:layout>
</fr:edit>

