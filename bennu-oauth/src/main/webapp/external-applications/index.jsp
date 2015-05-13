<%--

    Copyright © 2015 Instituto Superior Técnico

    This file is part of Bennu OAuth.

    Bennu OAuth is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bennu OAuth is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Bennu OAuth.  If not, see <http://www.gnu.org/licenses/>.

--%>
<div ng-app="bennuOAuth">
	<div ng-view>
	</div>
	${portal.angularToolkit()}
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/angular-translate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/angular-translate-loader-static-files.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/app.js"></script>
</div>