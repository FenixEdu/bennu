<div ng-app="bennuAdmin">
	<div ng-view>
	</div>
	${portal.angularToolkit()}
	<script>window.functionality = '${portal.selectedFunctionality(pageContext.request)}';</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/cropper/cropper.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/js/app.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/js/menu.js"></script>
</div>
