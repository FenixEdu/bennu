<div ng-app="bennuScheduler">
	<div ng-view>
	</div>
	${portal.angularToolkit()}
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-portal/js/angular-route.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/moment/moment.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/codemirror/codemirror.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-scheduler-ui/js/app.js"></script>
	<link href="${pageContext.request.contextPath}/bennu-scheduler-ui/js/libs/codemirror/codemirror.css" rel="stylesheet">
</div>