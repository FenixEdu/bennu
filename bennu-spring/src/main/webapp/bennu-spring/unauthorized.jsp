<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of Bennu Spring.

    Bennu Spring is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bennu Spring is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Bennu Spring.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%
response.setStatus(403);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Unauthorized</title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/portal.js"></script>
</head>
<body style="display: none">
	<div id="portal-container">
		<div class="container">
			<div class="text-center">
				<h1>Unauthorized</h1>
				<br />
				<p>This is not the page you're looking for!</p>
			</div>
		</div>
	</div>
</body>
