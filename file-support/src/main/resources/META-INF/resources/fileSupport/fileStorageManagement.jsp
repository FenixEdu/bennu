<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<%@page import="pt.ist.bennu.core.presentationTier.servlets.filters.contentRewrite.ContentContextInjectionRewriter"%>
<%@page import="pt.ist.fenixWebFramework.servlets.filters.contentRewrite.GenericChecksumRewriter"%>

<h2><bean:message key="title.fileStorageManagement" bundle="FILE_SUPPORT_RESOURCES"/></h2>

<script type="text/javascript">
	$(function() {
		$("#tabs").tabs();
	});
</script>

<fr:view schema="view.fileStorage.list" name="fileStorages" >
	<fr:layout name="tabular">
		<fr:property name="classes" value="tstyle2"/>
		<fr:property name="columnClasses" value="smalltxt bold,smalltxt,smalltxt,smalltxt,smalltxt"/>
		<fr:property name="sortBy" value="name"/>

        <fr:property name="customLink(convertFiles)">
            <html:link page="/fileStorageManagement.do?method=convertFileStorage&storageOID=${externalId}">
                <bean:message key="link.convertFileStorage" bundle="FILE_SUPPORT_RESOURCES"/>
            </html:link>
        </fr:property>
		
		<fr:property name="customLink(delete)">
			<html:link page="/fileStorageManagement.do?method=deleteStorage&storageOID=${externalId}">
	        	<bean:message key="link.delete" bundle="FILE_SUPPORT_RESOURCES"/>
			</html:link>
		</fr:property>
		<fr:property name="visibleIf(delete)" value="canBeDeleted"/>
		
	</fr:layout>
</fr:view>

<h2><bean:message key="title.fileStorageManagement.newStorage" bundle="FILE_SUPPORT_RESOURCES"/></h2>
<div id="tabs">
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.BLOCK_HAS_CONTEXT_PREFIX %>
	<ul>
		<li><%= GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a href="#tabs-1"><bean:message key="label.fileStorageManagement.domainStorage" bundle="FILE_SUPPORT_RESOURCES"/></a></li>
		<li><%= GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a href="#tabs-2"><bean:message key="label.fileStorageManagement.localFileSystemStorage" bundle="FILE_SUPPORT_RESOURCES"/></a></li>
		<li><%= GenericChecksumRewriter.NO_CHECKSUM_PREFIX %><a href="#tabs-3"><bean:message key="label.fileStorageManagement.dbStorage" bundle="FILE_SUPPORT_RESOURCES"/></a></li>
	</ul>
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.END_BLOCK_HAS_CONTEXT_PREFIX %>
	
	<div id="tabs-1">
		<fr:form action="/fileStorageManagement.do?method=createDomainStorage">
			<fr:edit schema="createDomainStorage" id="domainStorage" name="domainStorage" >
				<fr:layout name="tabular-editable">
					<fr:property name="classes" value="tstyle2"/>
					<fr:property name="columnClasses" value="smalltxt aleft, smalltxt"/>
				</fr:layout>
			</fr:edit>
			<html:submit styleClass="inputbutton"><bean:message key="button.create" bundle="FILE_SUPPORT_RESOURCES"/> </html:submit>
		</fr:form>
	</div>
	<div id="tabs-2">
		<fr:form action="/fileStorageManagement.do?method=createLocalFileSystemStorage">
			<fr:edit schema="createLocalFileSystemStorage" id="localFileSystemStorage" name="localFileSystemStorage">
				<fr:layout name="tabular-editable">
					<fr:property name="classes" value="tstyle2"/>
					<fr:property name="columnClasses" value="smalltxt aleft, smalltxt"/>
				</fr:layout>
			</fr:edit>
			<html:submit styleClass="inputbutton"><bean:message key="button.create" bundle="FILE_SUPPORT_RESOURCES"/> </html:submit>
		</fr:form>			
	</div>
	<div id="tabs-3">
		<fr:form action="/fileStorageManagement.do?method=createDBStorage">
			<fr:edit schema="createDBStorage" id="dbStorage" name="dbStorage" >
				<fr:layout name="tabular-editable">
					<fr:property name="classes" value="tstyle2"/>
					<fr:property name="columnClasses" value="smalltxt aleft, smalltxt"/>
				</fr:layout>
			</fr:edit>
			<html:submit styleClass="inputbutton"><bean:message key="button.create" bundle="FILE_SUPPORT_RESOURCES"/> </html:submit>
		</fr:form>
	</div>
</div>
