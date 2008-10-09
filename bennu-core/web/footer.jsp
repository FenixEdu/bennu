<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/taglibs-datetime.tld" prefix="dt" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<div class="c1"></div>
<div class="c2"></div>
<div class="c3"></div>
<div class="c4"></div>
<p>
	<a href="http://validator.w3.org/check?uri=referer" target="_blank">Valid XHTML</a>
	|
	<a href="http://jigsaw.w3.org/css-validator/check/referer" target="_blank">Valid CSS</a>
	<strong>
		&copy;<dt:format pattern="yyyy"><dt:currentTime/></dt:format> <bean:write name="myOrg" property="applicationCopyright"/>
	</strong>
</p>
