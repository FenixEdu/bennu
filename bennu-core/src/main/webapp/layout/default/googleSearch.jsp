<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr" %>

<logic:equal name="virtualHost" property="googleSearchEnabled" value="true">	
	<bean:define id="site" name="virtualHost" property="hostname"/>
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.BLOCK_HAS_CONTEXT_PREFIX %>
	<!-- NO_CHECKSUM -->
	<form method="get" action="http://www.google.com/search" id="globalsearch">
		<input type="hidden" name="site" value="<%= site %>" />
		<input type="hidden" name="hl" value="en" />
		<input type="hidden" name="btnG" value="Search" />
		<input type="hidden" name="domains" value="" />
		<input type="hidden" name="sitesearch" value="" />
		<input type="text" id="q" name="q" value="Search..." />
		<input class=" button" type="submit" name="Submit" value="Google" />
	</form>
	<%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.END_BLOCK_HAS_CONTEXT_PREFIX %>
</logic:equal>
