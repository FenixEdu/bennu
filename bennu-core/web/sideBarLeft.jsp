<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<ul id="navlist">
	<logic:empty name="menuNodes">
		<li><!-- NO_CHECKSUM --><a href="home.do?method=firstPage">Home</a></li>
	</logic:empty>
	<logic:notEmpty name="menuNodes">
		<logic:iterate id="node" name="menuNodes">
			<li>
				<a href="#"><bean:write name="node" property="childPage.link"/></a>
			</li>
		</logic:iterate>
	</logic:notEmpty>
	<li>
		<p>
			<strong>A tiny little service announcement.</strong>
			<br/>Put all your little tidbits of information or pictures in this small yet useful little area.
		</p>
	</li>
</ul>

