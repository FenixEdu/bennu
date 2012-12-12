<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>

<h2><bean:message key="title.storageConfigurationManagement" bundle="FILE_SUPPORT_RESOURCES"/></h2>

<fr:edit schema="edit.fileStorageConfigurations" name="storageConfigurations" >
	<fr:layout name="tabular-editable">
		<fr:property name="classes" value="tstyle2"/>
		<fr:property name="columnClasses" value="smalltxt aleft, smalltxt"/>
		<fr:property name="sortBy" value="fileType"/>
	</fr:layout>
</fr:edit>

