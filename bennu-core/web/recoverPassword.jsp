<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr"%>


<h2><bean:message bundle="MYORG_RESOURCES" key="title.recover.password"/> </h2> 

<fr:edit id="user" name="user" action="/home.do?method=firstPage">
	<fr:schema bundle="MYORG_RESOURCES" type="myorg.domain.User">
		<fr:slot name="password" key="label.new.password" layout="password">
			<fr:property name="size" value="40"/>
		</fr:slot>
	</fr:schema>
	<fr:layout name="tabular">
		<fr:property name="columnClasses" value=",,tderror" />
	</fr:layout>
</fr:edit>
<script type="text/javascript">
<!--
	document.getElementsById("<%= "myorg.domain.User:" + ((myorg.domain.User) request.getAttribute("user")).getExternalId() + ":password" %>").value = '';
//-->
</script>

