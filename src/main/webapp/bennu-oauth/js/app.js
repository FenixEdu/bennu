var bennuOAuth = angular.module('bennuOAuth', [
                                               'ngRoute', 'pascalprecht.translate'
                                               ]);

bennuOAuth.filter('scopeNames', function() {	
	return function(scopes) {
		var names = [];		
		angular.forEach(scopes, function(scope) {
			if (scope.selected) {
				names.push(scope.name);
			}
		});		
		return names.join(", ");
	}
});

bennuOAuth.factory('httpUnauthorizedFilter', ['$window', function httpUnauthorizedFilter($window) {

	return {
		responseError : function(response) {
			if (response.status === 401) { // unauthorized
				$window.location.href = window.contextPath + "/login";
			}
		}
	};
}]);

function createReloadableUrlObject(application, logoUrlField) {
	if (!logoUrlField) {
		logoUrlField = 'logoUrl';
	}

	if(!application[logoUrlField]) {
		return application;
	}
	application.getLogoUrl = application[logoUrlField] + "?cb=" + (new Date()).getTime();
	return application;
}

function getReloadableUrlObjects(data, logoUrlField) {
	angular.forEach(data, function(app) {
		createReloadableUrlObject(app, logoUrlField);
	});
	return data;
}

function fileNameChangedAux(e, type, $scope){
	var files = e.files; // FileList object
	$scope.error = '';
	for ( var i = 0; i < files.length; i++) {
		var file = files[i];
		if (!file.type.match("image.*")) {
			$scope.$apply(function () {
				$scope.error = "Only images";
			});
			continue;
		}
		if (file.size > 2000 * 1024) { // 2000kb
			$scope.$apply(function () {
				$scope.error = "Image too large. Maximum size : 2MB";
			});
			continue;
		}
		var reader = new FileReader();
		reader.onload = (function(f) {
			return function(e) {
				var content = e.target.result;
				var picBase64 = content.substr(content.indexOf(",") + 1, content.length);
				if(type === "logo"){
					$scope.$apply(function () {
						$scope.currentapp.logo = picBase64;
					});
				}
			};
		})(file);
		reader.readAsDataURL(file);
	}
}

bennuOAuth.config(['$routeProvider','$httpProvider','$translateProvider',
                   function($routeProvider, $httpProvider, $translateProvider) {
	$routeProvider.
	when('/authorizations', {
		templateUrl: contextPath + '/bennu-oauth/template/Authorizations.html',
		controller: 'AuthorizationsCtrl'
	}).
	when('/authorizations/:id', {
		templateUrl: contextPath + '/bennu-oauth/template/AuthorizationsById.html',
		controller: 'AuthorizationsByIdCtrl'
	}).   
	when('/applications', {
		templateUrl: contextPath + '/bennu-oauth/template/Applications.html',
		controller: 'ApplicationsCtrl'
	}).
	when('/service-applications', {
		templateUrl: contextPath + '/bennu-oauth/template/ServiceApplications.html',
		controller: 'ServiceApplicationsCtrl'
	}).
	when('/manage', {
		templateUrl: contextPath + '/bennu-oauth/template/Manage.html',
		controller: 'ManageCtrl'
	}).
	otherwise({
		redirectTo: '/authorizations'
	});

	$httpProvider.interceptors.push('httpUnauthorizedFilter');
	
	$translateProvider.useStaticFilesLoader({
        prefix: window.contextPath + "/bennu-oauth/i18n/",
        suffix: '.json'
    });
	
	$translateProvider.preferredLanguage(Bennu.locale.lang);
        	
}]);

bennuOAuth.controller('AuthorizationsCtrl', [ '$scope', '$http', '$location', function ($scope, $http, $location) {

	$scope.ctx = contextPath;

	$scope.predicate = 'applicationName';

	$scope.create = function() {
		$http.post(contextPath + '/api/bennu-oauth/authorizations', {'name': $scope.name}).success(function (data) {
			$scope.authorizations.push(createReloadableUrlObject(data, 'applicationLogoUrl')); 
			$scope.name = "";
		});
	};	

	$http.get(contextPath + '/api/bennu-oauth/authorizations').success(function (data) {
		$scope.authorizations = getReloadableUrlObjects(data, 'applicationLogoUrl');
	});

	$scope.showRevokeModal = function(authorization) {
		$scope.selectedAuthorization = authorization;
		$('#modal-revoke-menu').modal('show');
	};

	$scope.showDetails = function(authorization) {
		$scope.selectedAuthorization = authorization;
		$location.url('/authorizations/'+authorization.id);
	};

	$scope.revokeApp = function() {
		var index = $scope.authorizations.indexOf($scope.selectedAuthorization);
		$http.delete(contextPath + '/api/bennu-oauth/authorizations/' + $scope.selectedAuthorization.id).success(function () {
			if (index > -1) {
				$scope.authorizations.splice(index, 1);
			}
		});
	};

	$scope.filterApplications = function(value, index) {
		var found = false;
		angular.forEach(['applicationName', 'applicationAuthor', 'applicationDescription', 'applicationSiteUrl'], function(key) {
			if (!$scope.query || value[key].toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
				found = true;
				return;
			}
		});
		return found;
	};
}]);

bennuOAuth.controller('AuthorizationsByIdCtrl', [ '$scope', '$http', '$routeParams', '$location', function ($scope, $http, $routeParams, $location) {

	$scope.ctx = contextPath;

	$scope.predicate = '-date';

	$http.get(contextPath + '/api/bennu-oauth/sessions/' + $routeParams.id ).success(function (data) {
		$scope.sessions = data;		
	});	

	$scope.showRevokeSessionModal = function(session) {
		$scope.selectedSession = session;
		$('#modal-revoke-session-menu').modal('show');
	};

	$scope.revokeSession = function() {	
		$http.delete(contextPath + '/api/bennu-oauth/sessions/' + $scope.selectedSession.id).success(function () {
			$http.get(contextPath + '/api/bennu-oauth/sessions/' + $routeParams.id ).success(function (data) {
				$scope.sessions = data;		
			});	
		});
	};

}]);

bennuOAuth.controller('ApplicationsCtrl', [ '$scope', '$http', '$cacheFactory', '$timeout', function ($scope, $http, $cacheFactory, $timeout) {

	$scope.ctx = contextPath;

	$scope.predicate = 'name';
	$scope.predicateScope = 'name';

	$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {		
		$scope.applications = getReloadableUrlObjects(data);
	});

	$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
		$scope.scopes = data;
	});

	$scope.create = function() {
		$('#createApplication').modal('hide');
		$http.post(contextPath + '/api/bennu-oauth/applications', {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'siteUrl': $scope.currentapp.siteUrl, 'redirectUrl': $scope.currentapp.redirectUrl, 'logo': $scope.currentapp.logo, 'scopes': $scope.currentapp.scopes}).success(function (data) {
			$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {
				$scope.applications = getReloadableUrlObjects(data);
			});
		});
	};

	$scope.update = function() {
		$('#editApplication').modal('hide');
		$http.put(contextPath + '/api/bennu-oauth/applications/' +  $scope.currentapp.id, {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'siteUrl': $scope.currentapp.siteUrl, 'redirectUrl': $scope.currentapp.redirectUrl, 'logo': $scope.currentapp.logo, 'scopes': $scope.currentapp.scopes }).success(function (data) {
			if ($scope.currentappindex > -1) {
				$scope.applications[$scope.currentappindex] = createReloadableUrlObject(data);
			}
		});
	};

	$scope.deleteApp = function() {
		var index = $scope.applications.indexOf($scope.currentapp);
		$http.delete(contextPath + '/api/bennu-oauth/applications/' + $scope.currentapp.id).success(function () {
			if (index > -1) {
				$scope.applications.splice(index, 1);				
			}			
		});
	};

	$scope.showCreateApplication = function() {
		angular.element('#logo').val(null);
		$scope.error = "";
		$scope.currentapp = {logo : null, name : "", scopes : [], description : "" , siteUrl : "", redirectUrl : ""};
		$scope.currentapp.scopes = angular.copy($scope.scopes);		
		$('#createApplication').modal('show');
	};

	$scope.showDetailsApplication = function(application) {
		$scope.currentappindex = $scope.applications.indexOf(application);
		$scope.currentapp = angular.copy(application);
		$('#detailsApplication').modal('show');
	};

	$scope.showEditApplication = function(application) {
		angular.element('#editlogo').val(null);
		$scope.error = "";
		$scope.currentappindex = $scope.applications.indexOf(application);
		$scope.currentapp = angular.copy(application);
		$scope.currentapp.scopes = application.scopes;		
		$('#editApplication').modal('show');	
	};

	$scope.showDeleteModal = function(application) {
		$scope.currentappindex = $scope.applications.indexOf(application);
		$scope.currentapp = application;
		$('#modal-delete-menu').modal('show');
	};

	$scope.fileNameChanged = function(e, type) {
		fileNameChangedAux(e, type, $scope);
	};

	$scope.filterApplications = function(value, index) {
		var found = false;
		angular.forEach(['name', 'description', 'siteUrl'], function(key) {
			if (!$scope.query || value[key].toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
				found = true;
				return;
			}
		});
		return found;
	};

}]);

bennuOAuth.controller('ManageCtrl', [ '$scope', '$http', function ($scope, $http) {

	$scope.ctx = contextPath;
	
	$scope.applicationsBaseApiUrl = $scope.applicationsBaseApiUrl || '/api/bennu-oauth/applications';

	$scope.predicate = 'scopeKey';
	$scope.predicateApplications = 'name';


	$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
		$scope.scopes = data;
	});

	$http.get(contextPath + $scope.applicationsBaseApiUrl + '/all').success(function (data) {
		$scope.applications = getReloadableUrlObjects(data);
		//$scope.originalApplications = getReloadableUrlObjects(data);
	});

	$scope.create = function() {
		$('#addScope').modal('hide');
		$http.post(contextPath + '/api/bennu-oauth/scopes', {'scopeKey': $scope.currentscope.scopeKey, 'name': $scope.currentscope.name, 'description': $scope.currentscope.description}).success(function (data) {
			$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
				$scope.scopes = data;
			});
		});
	};

	$scope.update = function() {
		$('#editScope').modal('hide');
		$http.put(contextPath + '/api/bennu-oauth/scopes/' + $scope.currentscope.id, {'name': $scope.currentscope.name, 'description': $scope.currentscope.description}).success(function () {			
			$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
				$scope.scopes = data;
			});
		});
	};

	$scope.deleteScope = function() {		
		$http.delete(contextPath + '/api/bennu-oauth/scopes/' + $scope.currentscope.id).success(function () {			
			$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
				$scope.scopes = data;
			});
		});
	};

	$scope.updateApplication = function() {
		$('#editApplicationManager').modal('hide');
		$http.put(contextPath + $scope.applicationsBaseApiUrl +  +  $scope.currentapp.id, {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'author': $scope.currentapp.author, 'siteUrl': $scope.currentapp.siteUrl, 'redirectUrl': $scope.currentapp.redirectUrl, 'logo': $scope.currentapp.logo, 'scopes': $scope.currentapp.scopes }).success(function (data) {
			if ($scope.currentappindex > -1) {
				$scope.applications[$scope.currentappindex] = createReloadableUrlObject(data);
			}
		});
	};

	$scope.fileNameChanged = function(e, type) {
		fileNameChangedAux(e, type, $scope);
	};

	$scope.showDeleteModal = function(scope) {
		$scope.currentscope = Object.create(scope);
		$('#modal-delete-menu').modal('show');
	};

	$scope.showEditModal = function(scope) {
		$scope.currentscope = Object.create(scope);
		$('#editScope').modal('show');
	};


	$scope.showCreateScope = function() {
		$scope.currentscope = {};
		$('#addScope').modal('show');
	};

	$scope.showBanModal = function(application) {
		$scope.currentapp = application;
		$('#modal-ban-menu').modal('show');
	};

	$scope.showUnbanModal = function(application) {
		$scope.currentapp = application;
		$('#modal-unban-menu').modal('show');
	};

	$scope.showDeleteApplicationModal = function(application) {
		$scope.currentapp = application;
		$('#modal-delete-application-menu').modal('show');
	};

	$scope.banApplication = function() {
		var index = $scope.applications.indexOf($scope.currentapp);
		$http.put(contextPath + $scope.applicationsBaseApiUrl + $scope.currentapp.id + '/ban').success(function () {
			$scope.applications[index].state = "Banned";
		});
	};

	$scope.activeApplication = function() {
		var index = $scope.applications.indexOf($scope.currentapp);
		$http.put(contextPath + $scope.applicationsBaseApiUrl + $scope.currentapp.id + '/active').success(function () {
			$scope.applications[index].state = "Active";
		});
	};

	$scope.deleteApplication = function() {
		var index = $scope.applications.indexOf($scope.currentapp);
		$http.delete(contextPath + $scope.applicationsBaseApiUrl + $scope.currentapp.id).success(function () {
			$scope.applications[index].state = "Deleted";
		});
	};

	$scope.filterApplications = function(value, index) {
		var found = false;
		angular.forEach(['name', 'description', 'author', 'state', 'redirectUrl'], function(key) {
			if (!$scope.query || value[key].toLowerCase().indexOf($scope.query.toLowerCase()) >= 0) {
				found = true;
				return;
			}
		});
		return found;
	};

	$scope.filterScopes = function(value, index) {
		var found = false;
		angular.forEach(['scopeKey', 'name'], function(key) {
			if (!$scope.queryScopes || value[key].toLowerCase().indexOf($scope.queryScopes.toLowerCase()) >= 0) {
				found = true;
				return;
			}
		});
		return found;
	};

	$scope.showDetailsApplication = function(application) {
		$scope.currentapp = application;
		$('#detailsApplication').modal('show');
	};

	$scope.showEditApplicationModal = function(application) {
		angular.element('#logo').val(null);
		$scope.currentappindex = $scope.applications.indexOf(application);
		$scope.currentapp = angular.copy(application);
		$('#editApplicationManager').modal('show');
	};

	$scope.btnFilter = function(state, type) {
		if (type == 'Edit') {
			if(state != 'Deleted') {
				return true;
			}
		} else if (type == 'Ban') {
			if(state != 'Deleted' && state != 'Banned') {
				return true;
			}
		} else if (type == 'Unban') {			
			if(state != 'Deleted' && state == 'Banned') {
				return true;
			}			
		} else if (type == 'Delete') {
			if(state != 'Deleted') {
				return true;
			}
		}
		return false;
	}

	$scope.keyChange = function() {
		var found = false;

		angular.forEach($scope.scopes, function(scope) {
			if(scope.scopeKey === $scope.currentscope.scopeKey) {
				found = true;
			} 
		});
		$scope.error = found;

	};
}]);

bennuOAuth.controller('ServiceApplicationsCtrl', ['$scope', '$http', '$controller', function ($scope, $http, $controller) {
	
	$scope.applicationsBaseApiUrl = '/api/bennu-oauth/service-applications';
		
	$controller('ManageCtrl', {$scope : $scope});
	
	$scope.showCreateApplication = function() {
		angular.element('#logo').val(null);
		$scope.error = "";
		$scope.currentapp = {logo : null, name : "", scopes : [], description : "" , siteUrl : "", redirectUrl : ""};
		$scope.currentapp.scopes = angular.copy($scope.scopes);		
		$('#createApplication').modal('show');
	};
	
	$scope.createApplication = function() {
		$('#createApplication').modal('hide');
		$http.post(contextPath + $scope.applicationsBaseApiUrl, {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'siteUrl': $scope.currentapp.siteUrl, 'logo': $scope.currentapp.logo}).success(function (data) {
			$http.get(contextPath + $scope.applicationsBaseApiUrl + '/all').success(function (data) {
				$scope.applications = getReloadableUrlObjects(data);
			});
		});
	};
	
}]);