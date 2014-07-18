<div ng-app="bennuAdmin">
	<div ng-view>
	</div>
	${portal.bennuPortal()}
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/cropper/cropper.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/js/app.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/js/menu.js"></script>
</div>
