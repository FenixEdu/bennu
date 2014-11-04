var bennuOAuth = angular.module('bennuOAuth', [
                                               'ngRoute'
                                               ]);

bennuOAuth.config(['$routeProvider',
                   function($routeProvider) {
	$routeProvider.
	when('/authorizations', {
		templateUrl: contextPath + '/bennu-oauth/template/Authorizations.html',
		controller: 'AuthorizationsCtrl'
	}).      
	when('/applications', {
		templateUrl: contextPath + '/bennu-oauth/template/Applications.html',
		controller: 'ApplicationsCtrl'
	}).
	when('/manage', {
		templateUrl: contextPath + '/bennu-oauth/template/Manage.html',
		controller: 'ManageCtrl'
	}).
	otherwise({
		redirectTo: '/authorizations'
	});
}]);

bennuOAuth.controller('AuthorizationsCtrl', [ '$scope', '$http', function ($scope, $http) {
	$scope.ctx = contextPath;
	$scope.create = function() {
		$http.post(contextPath + '/api/bennu-oauth/authorizations', {'name': $scope.name}).success(function (data) {
			$scope.authorizations.push(data); $scope.name = "";
		});
	};	

	$http.get(contextPath + '/api/bennu-oauth/authorizations').success(function (data) {
		$scope.authorizations = data;
	});
	
	$scope.showRevokeModal = function(authorization) {
		$scope.selectedAuthorization = authorization;
		$('#modal-revoke-menu').modal('show');
	}
	
	$scope.revokeApp = function() {
		var index = $scope.authorizations.indexOf($scope.selectedAuthorization);
		$http.delete(contextPath + '/api/bennu-oauth/authorizations/' + $scope.selectedAuthorization.id).success(function () {
			if (index > -1) {
				$scope.authorizations.splice(index, 1);
			}
		});
	}	
}]);

bennuOAuth.controller('ApplicationsCtrl', [ '$scope', '$http', function ($scope, $http) {
	$scope.ctx = contextPath;

	$scope.create = function(type) {
		if($scope.type === "create") {
			$http.post(contextPath + '/api/bennu-oauth/applications', {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'siteUrl': $scope.currentapp.siteUrl, 'redirectUrl': $scope.currentapp.redirectUrl, 'logo': $scope.currentapp.logo, 'scopes': $scope.currentapp.selectedScopesCheckbox}).success(function (data) {
				$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {
					$scope.applications = data;
				});
				$('#logo').val('');					
			});

		} else if ($scope.type === "edit") {
			$http.put(contextPath + '/api/bennu-oauth/applications/' +  $scope.id, {'name': $scope.currentapp.name, 'description': $scope.currentapp.description, 'siteUrl': $scope.currentapp.siteUrl, 'redirectUrl': $scope.currentapp.redirectUrl, 'logo': $scope.currentapp.logo, 'scopes': $scope.currentapp.selectedScopesCheckbox }).success(function (data) {
				$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {
					$scope.applications = data;
				});
				$('#logo').val('');			
			});

		}
	};

	$scope.deleteApp = function() {
		var index = $scope.applications.indexOf($scope.selectedApp);
		$http.delete(contextPath + '/api/bennu-oauth/applications/' + $scope.selectedApp.id).success(function () {
			$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {
				$scope.applications = data;
			});
		});
	}

	$http.get(contextPath + '/api/bennu-oauth/applications').success(function (data) {
		$scope.applications = data;
	});

	$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
		$scope.scopes = data;
	});	


	$scope.selectedScopes = function () {
		getSelectedScopes();
	}

	$scope.showDeleteModal = function(app) {
		$scope.selectedApp = app;
		$('#modal-delete-menu').modal('show');
	}

	$scope.fileNameChanged = function(e, type) {
		var files = e.files; // FileList object
		$scope.error = '';
		for ( var i = 0; i < files.length; i++) {
			var file = files[i];
			if (!file.type.match("image.*")) {
				$scope.error = "<p>Apenas são aceites imagens</p>";
				continue;
			}
			if (file.size > 2000 * 1024) { // 2000kb
				$scope.error = "<p>Imagem muito grande. Tamanho máximo : 200kb</p>";
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
	
	$scope.showCreateApplication = function() {
		$scope.type = 'create';
		$scope.typeStr = 'Create Application';
		clearScopes();
		$scope.currentapp = {};

		$('#createApplication').modal('show');
	}

	$scope.showDetailsApplication = function(application) {
		$scope.currentapp = Object.create(application);
		clearScopes();
		getSelectedScopes();
		$('#detailsApplication').modal('show');

	}
	
	
	$scope.showEditApplication = function(application) {

		$scope.currentapp = Object.create(application);
		clearScopes();
		getSelectedScopes();
		$('#createApplication').modal('show');

		$scope.selectedApp = application;
		$scope.id = application.id;
		$scope.type = 'edit';
		$scope.typeStr = 'Edit Application';	

		var scopesSelectedArray = application.scopesId.split(", ");
		var scopesArray = [];
		var arrayLength = $scope.scopes.length;			
		for (var i = 0; i < arrayLength; i++) {			   
			scopesArray.push($scope.scopes[i].id);
		}

		var selectedScopeArray = intersect(scopesArray, scopesSelectedArray);

		var arrayLengthSelected = selectedScopeArray.length;

		for (var i = 0; i < arrayLengthSelected; i++) {	
			
			$('#'+selectedScopeArray[i]).prop('checked', true);
		}
		getSelectedScopes();

	}	

	function getUpdateScopes() {
		
	}
	
	function getSelectedScopes() {
		var selScopes = []
		$scope.scopes.forEach(function(entry) {
			if ($('#'+entry.id).prop('checked')) {	
				selScopes.push(entry.id);	
			}
		});
		$scope.currentapp.selectedScopesCheckbox = selScopes;
	}

	function clearScopes() {
		var arrayLength = $scope.scopes.length;	
		for (var i = 0; i < arrayLength; i++) {			    
			$('#'+$scope.scopes[i].id).prop('checked', false);
		}
	}

	function intersect(a, b) {
		var t;
		if (b.length > a.length) t = b, b = a, a = t; 
		return a.filter(function (e) {
			if (b.indexOf(e) !== -1) return true;
		});	    
	}

}]);

bennuOAuth.controller('ManageCtrl', [ '$scope', '$http', function ($scope, $http) {
	$scope.ctx = contextPath;
	$scope.create = function() {		
		$http.post(contextPath + '/api/bennu-oauth/scopes', {'scopeKey': $scope.scopeKey, 'name': $scope.name, 'description': $scope.description}).success(function (data) {
			$scope.scopes.push(data); 
			$scope.scopeKey = "";
			$scope.name = "";
			$scope.description = "";
		});
	};
	$scope.deleteScope = function() {		
		var index = $scope.scopes.indexOf($scope.selectedScope);
		var arr = 
			$http.delete(contextPath + '/api/bennu-oauth/scopes/' + $scope.selectedScope.id).success(function () {			
				if (index > -1) {
					$scope.scopes.splice(index, 1);
				}
			});
	}
	$scope.showDeleteModal = function(scope) {
		$scope.selectedScope = scope;
		$('#modal-delete-menu').modal('show');
	}
	$scope.showEditModal = function(scope) {
		$('#editScopeKey').val(scope.scopeKey);
		$('#editName').val(scope.name);
		$('#editDescription').val(scope.description);
		$scope.selectedScope = scope;
	}

	$http.get(contextPath + '/api/bennu-oauth/scopes').success(function (data) {
		$scope.scopes = data;
	});

	$http.get(contextPath + '/api/bennu-oauth/all-applications').success(function (data) {
		$scope.applications = data;
	});	

}]);