<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>

<logic:present name="selectedNode">
</logic:present>
<logic:notPresent name="selectedNode">
	<h2>Welcome</h2>
	<p>
		This application has not yet been configured. To do so please log in with a user that has management priveledges, and start creating contents.
		Remember that you cad define which users have management roles in your build.properties file.
	</p>
</logic:notPresent>
