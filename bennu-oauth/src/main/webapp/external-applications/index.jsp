<div ng-app="bennuOAuth">
	<div ng-view>
	</div>
	${portal.angularToolkit()}
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/angular-translate.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/angular-translate-loader-static-files.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/app.js"></script>
</div>