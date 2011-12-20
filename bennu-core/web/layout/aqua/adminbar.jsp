<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/fenix-renderers.tld" prefix="fr" %>
<%@page import="pt.ist.fenixWebFramework.security.User"%>

<logic:present name="USER_SESSION_ATTRIBUTE">
<%
	final User user = (User) request.getSession(false).getAttribute("USER_SESSION_ATTRIBUTE");
	if (user.hasRole("myorg.domain.RoleType.MANAGER")) {
%>
<style type="text/css">
html { margin-top: 28px !important; }
* html body { margin-top: 28px !important; }
</style>
<nav id="admin-bar">
	<ul class="admin-bar-top">
		<li><span class="nav-title">ADMIN BAR</span></li>
	    <li class="contains-submenu">
	        <%= pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestRewriter.HAS_CONTEXT_PREFIX %><html:link page="/configuration.do?method=applicationConfiguration">
			<bean:message bundle="MYORG_RESOURCES" key="label.application.configuration"/>
			</html:link>
			<ul class="admin-bar-sub">
	            <li><html:link action="/configuration.do?method=manageSystemGroups">Grupos de Sistema</html:link></li>  
	            <li><html:link action="/scheduler.do?method=viewScheduler">Agendamento de Tarefas</html:link></li>
	            <li><html:link action="/configuration.do?method=viewSystemConfig">Propriedades de Sistema</html:link></li>
	        </ul>
	    </li>
		<li class="contains-submenu">
			<a href="#">Sources</a>
			<ul class="admin-bar-sub">
				<li>
					<a href="#management/sources">Manage Sources</a>
				</li>
				<li>
					<a href="#management/strategies">Manage Strategies</a>
				</li>
				<li>
					<a href="#management/logs">View Logs</a>
				</li>
			</ul>
		</li>
		<li class="contains-submenu">
			<a href="#">Infrastructure</a>
			<ul class="admin-bar-sub">
				<li>
					<a href="#management/library">Bibliotecários</a>
				</li>
				<li>
					<a href="#management/entities">Pessoas</a>
				</li>
				<li>
					<a href="#management/schema">Schemas</a>
				</li>
				<li>
					<a href="#library/referenceformat">Reference Formats</a>
				</li>
			</ul>
		</li>
	</ul>
</nav>
<%	} %>
</logic:present>

