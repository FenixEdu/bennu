<!DOCTYPE html>
<html lang="en" ng-app="bennuIO">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>IO Management</title>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/portal.js"></script>
</head>
<body style="display: none">
	<div id="portal-container">
		<div ng-view>
		</div>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-io-ui/js/app.js"></script>
	</div>
</body>
</html>
