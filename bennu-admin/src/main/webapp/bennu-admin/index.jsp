<div ng-app="bennuAdmin">
	<h2 ng-class="'page-header'" ng-show="pageTitle" ng-bind="pageTitle">
	</h2>
	<div ui-view>
	</div>
	${portal.angularToolkit()}
	<script>window.functionality = '${portal.selectedFunctionality(pageContext.request)}';
	window.functionality||(location.href=BennuPortal.username?contextPath:contextPath+"/login?callback\x3d"+escape(location.href));</script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/libs/angular-ui-router.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/js/app.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-admin/libs/codemirror/codemirror.js"></script>
	<link href="${pageContext.request.contextPath}/bennu-admin/libs/codemirror/codemirror.css" rel="stylesheet">
</div>
