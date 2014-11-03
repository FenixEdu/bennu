<div ng-app="bennuOAuth">
	<div ng-view>
	</div>
	${portal.bennuPortal()}
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-oauth/js/app.js"></script>
</div>