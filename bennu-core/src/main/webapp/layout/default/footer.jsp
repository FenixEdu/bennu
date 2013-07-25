<%@page isELIgnored="false"%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.joda.org/joda/time/tags" prefix="joda" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>

<div class="c1"></div>
<div class="c2"></div>
<div class="c3"></div>
<div class="c4"></div>

<p>
    <% pageContext.setAttribute("now", new org.joda.time.DateTime()); %>
	&copy;<joda:format value="${now}" pattern="yyyy"/> <bean:write name="virtualHost" property="applicationCopyright"/>
</p>