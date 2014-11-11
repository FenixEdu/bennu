var bennuIO = angular.module('bennuIO', [ 'ngRoute', 'bennuToolkit' ]);

bennuIO.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/storage', {
        templateUrl: contextPath + '/bennu-io-ui/template/Storage.html',
        controller: 'StorageCtrl'
      }).
      when('/configuration', {
        templateUrl: contextPath + '/bennu-io-ui/template/Configuration.html',
        controller: 'ConfigurationCtrl'
      }).
      otherwise({
        redirectTo: '/storage'
      });
  }]);

bennuIO.controller('StorageCtrl', ['$scope', '$http', function ($scope, $http) {
  $http.get(contextPath + '/api/bennu-io/storage').success(function (data) {
    $scope.storages = data.storages;
  });
  $scope.delete = function(storage) {
    $http.delete(contextPath + '/api/bennu-io/storage/' + storage.id).success(function () {
      $scope.storages.splice($scope.storages.indexOf(storage), 1);
    }).error(function (data) { $scope.error = data; });
  }
  $scope.newDomainStorage = function() {
    $http.post(contextPath + '/api/bennu-io/storage/domain/' + $scope.domainName).success(function (data) {
      $scope.domainName = null; $scope.storages.push(data);
    }).error(function (data) { $scope.error = data; });
  }
  $scope.newFSStorage = function() {
    $http.post(contextPath + '/api/bennu-io/storage/lfs', 
              {'name' : $scope.fsName, 'path': $scope.path, 'treeDirectoriesNameLength': $scope.treeLen}).
              success(function (data) {
                $scope.fsName = $scope.path = $scope.treeLen = null; $scope.storages.push(data);
              }).error(function (data) { $scope.error = data; });
  }
  $scope.convert = function(storage) {
    $http.put(contextPath + '/api/bennu-io/storage/convert/' + storage.id);
  }
  $scope.setDefault = function(storage) {
    $http.post(contextPath + '/api/bennu-io/storage/default/' + storage.id).success(function (data) {
      $scope.storages = data.storages;
    }).error(function (data) { $scope.error = data; });
  }
}]);

bennuIO.controller('ConfigurationCtrl', ['$scope', '$http', function ($scope, $http) {
  $http.get(contextPath + '/api/bennu-io/storage').success(function (data) {
    $scope.storages = data.storages;
    $http.get(contextPath + '/api/bennu-io/storage/config').success(function (configs) {
      $scope.configs = configs.storageConfigurations;
    });
  });
  $scope.save = function() {
    $scope.success = false;
    var configs = Array();
    $scope.configs.forEach(function (config) {
      configs.push({ 'fileStorageConfigurationId': config.id, 'fileStorageId': config.storage.id });      
    });
    $http.post(contextPath + '/api/bennu-io/storage/config', configs).success(function (data) {
      $scope.configs = data.storageConfigurations;
      $scope.success = true;
    });
  }
}]);
