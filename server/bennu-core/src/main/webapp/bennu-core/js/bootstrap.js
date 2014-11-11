angular.module('bootstrapModule', [])
	.controller('BootstrapCtrl', 
		function ($scope, $http) {
		
		$http.get(ctxPath + '/api/bennu-core/bootstrap').success(function(data, status, headers, config) {
			$scope.bootstrappers = data.bootstrappers;
			$scope.availableLocales = data.availableLocales;
			$scope.currentBootstrapperNumber = 0;
			$scope.currentSectionNumber = 0;
		});

		$scope.submitWizard = function() {
			clearErrors();
			if(validateCurrentStep()) {
				$scope.submitting = true;
				$http.post(ctxPath + '/api/bennu-core/bootstrap', allFields()).
					success(function(data, status, headers, config) {
						window.location = ctxPath;
					}).
					error(function(data, status, headers, config) {
						if(status == 412) {
							showErrors(data);
							$scope.submitting = false;
						} else {
							$scope.exception = data;
						}
					});
			}
		};

		function showErrors(errors) {
			$.each(errors.reverse(), function(errorIndex, error) {
				if(error.fieldKey) {
					showFieldError(error.fieldKey, error);
				} else {
					$scope.errors.push(error);
				}
			});
		}

		$scope.submitInitialConfig = function() {
			$scope.locale = $scope.tempLocale;
		}

		$scope.showInitialConfig = function() {
			$scope.locale = null;
		}

		$scope.hasAnyError = function() {
			return $scope.error != null;
		}

		function validateCurrentStep() {
			var valid = true;
			var fields = $scope.getCurrentStep().fields;
			$.each(fields, function(fieldIndex, field) {
				if(!field.value) {
					field.hasError = true;
					field.hasRequiredError = true;
					valid = false;
				} else {
					field.hasError = false;
					field.hasRequiredError = false;
				}
			});
			return valid;
		}

		$scope.hasSections = function() {
			return $scope.bootstrappers[$scope.currentBootstrapperNumber]!=null && 
				$scope.bootstrappers[$scope.currentBootstrapperNumber].sections!=null &&
				$scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length!=0;
		};

		$scope.getCurrentStep = function() {
			return $scope.bootstrappers[$scope.currentBootstrapperNumber].sections[$scope.currentSectionNumber];
		};

		$scope.nextStep = function() {
			if(validateCurrentStep()) {
				if($scope.currentSectionNumber < $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1) {
					$scope.currentSectionNumber++;
				} else {
					if($scope.currentBootstrapperNumber < $scope.bootstrappers.length-1) {
						$scope.currentBootstrapperNumber++;
						$scope.currentSectionNumber = 0;
					}
				}
			}
		};

		$scope.advance = function() {
			$scope.lastBootstrapper() ? $scope.submitWizard() : $scope.nextStep();
		}

		$scope.previousStep = function() {
			if($scope.currentSectionNumber > 0) {
				$scope.currentSectionNumber--;
			} else {
				if($scope.currentBootstrapperNumber > 0) {
					$scope.currentBootstrapperNumber--;
					$scope.currentSectionNumber = $scope.bootstrappers[$scope.currentBootstrapperNumber].sections.length-1;
				}
			}
		};

		$scope.lastBootstrapper = function() {
			return $scope.currentBootstrapperNumber == $scope.bootstrappers.length-1;
		};
		
		$scope.firstBootstrapper = function() {
			return $scope.currentBootstrapperNumber == 0;
		};

		function mapIndex(mappingFunction) {
			$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper){
				$.each(bootstrapper.sections, function(sectionIndex, section){
					$.each(section.fields, function(fieldIndex, field) {
						mappingFunction(bootstrapperIndex, sectionIndex, fieldIndex);
					});
				});
			});
		}

		function mapValues(mappingFunction) {
			$.each($scope.bootstrappers, function(bootstrapperIndex, bootstrapper){
				$.each(bootstrapper.sections, function(sectionIndex, section){
					$.each(section.fields, function(fieldIndex, field) {
						mappingFunction(bootstrapper, section, field);
					});
				});
			});
		}

		function allFields() {
			var fields = new Object();
			mapValues(function(bootstrapper, section, field){
				if(!field.value) field.value = "";
				fields[field.key] = field.value;
			});
			return fields;
		}

		function showFieldError(fieldKey, fieldError) {
			mapIndex(function(bootstrapperIndex, sectionIndex, fieldIndex){
				var field = $scope.bootstrappers[bootstrapperIndex].sections[sectionIndex].fields[fieldIndex];
				if(angular.equals(fieldKey, field.key)) {
					$scope.currentBootstrapperNumber = bootstrapperIndex;
					$scope.currentSectionNumber = sectionIndex;
					field.hasError = true;
					field.error = fieldError;
				}
			});
		}

		function clearErrors() {
			$scope.errors = [];
			mapValues(function(bootstrapper, section, field){
				field.hasError = false;
				field.error = null;
			});
		}
	}).config(['$httpProvider',function($httpProvider) {
		$httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
		$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	}]);