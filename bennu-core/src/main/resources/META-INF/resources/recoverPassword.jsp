<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic"%>
<%@ taglib uri="http://fenix-ashes.ist.utl.pt/fenix-renderers" prefix="fr"%>


<h2><bean:message bundle="MYORG_RESOURCES" key="title.recover.password"/> </h2> 

<fr:edit id="user" name="user" action="/home.do?method=firstPage">
	<fr:schema bundle="MYORG_RESOURCES" type="pt.ist.bennu.core.domain.User">
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
	document.getElementById("<%= "pt.ist.bennu.core.domain.User:" + ((pt.ist.bennu.core.domain.User) request.getAttribute("user")).getExternalId() + ":password" %>").setAttribute("value", '');
//-->
</script>

