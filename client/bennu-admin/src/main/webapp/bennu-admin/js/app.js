var bennuAdmin = angular.module('bennuAdmin', [
  'ngRoute'
]);

bennuAdmin.config(['$compileProvider', function($compileProvider) {
  $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|data):/);
}]);

var i18n = function(input) {
  var tag = BennuPortal.locale.tag;
  if(input && input[tag]) {
    return input[tag];
  }
  return '!!' + JSON.stringify(input) + '!!';
}

function chunk(arr, n) {
    return arr.slice(0,(arr.length+n-1)/n|0).
           map(function(c,i) { return arr.slice(n*i,n*i+n); });
}

bennuAdmin.filter('i18n', function () {
  return i18n;
});

bennuAdmin.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/configuration', {
        templateUrl: 'template/PortalConfiguration.html',
        controller: 'PortalConfigurationCtrl'
      }).
      when('/menu/:id', {
        templateUrl: 'template/Menu.html',
        controller: 'MenuController'
      }).
      when('/system/info', {
        templateUrl: 'template/SystemInfo.html',
        controller: 'SystemInfoController'
      }).
      when('/system/logger', {
        templateUrl: 'template/Logger.html',
        controller: 'LoggerController'
      }).
      when('/domain-browser/:oid?', {
        templateUrl: 'template/DomainBrowser.html',
        controller: 'DomainBrowserController'
      }).
      otherwise({
        redirectTo: '/configuration'
      });
  }]);

bennuAdmin.controller('PortalConfigurationCtrl', ['$scope', '$http', function ($scope, $http) {
  $http.get('../api/bennu-portal/configuration').success(function (data) {
    $scope.locales = BennuPortal.locales;
    $scope.menu = data;
  });
  $scope.error = '';
  $scope.save = function() {
    $http.put('../api/bennu-portal/configuration/' + $scope.menu.id, $scope.menu).success(function () {
      location.reload();
    });
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
      if (file.size > 200 * 1024) { // 200kb
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
                      $scope.menu.logo = picBase64;
                      $scope.menu.logoType = file.type;
                    });
              }
              if(type === "favicon"){
            	  $scope.$apply(function () {
                      $scope.menu.favicon = picBase64;
                      $scope.menu.faviconType = file.type;
                    });
              }
          };
      })(file);
      reader.readAsDataURL(file);
    }
  }
}]);

bennuAdmin.controller('SystemInfoController', [ '$scope', '$http', function ($scope, $http) {
  $scope.reload = function() {
    $http.get('../api/bennu-core/system/info').success(function (data) {
      $scope.data = data;
    });
  }
  $scope.threadDump = function() {
    $http.get('../api/bennu-core/system/thread-dump').success(function (data) {
      $scope.threads = data;
    });
  }
  $scope.healthcheck = function() {
    $http.get('../api/bennu-core/system/healthcheck').success(function (data) {
      $scope.healthchecks = data; $scope.currentCheck = null;
    });
  }
  $scope.setCheck = function(check) { $scope.currentCheck = check; };
  $scope.getLabelClass = function(threadState) {
    if (threadState == 'RUNNABLE') {
        return "label-success";
    } else if (threadState == 'WAITING') {
        return "label-info";
    } else if (threadState == 'TIMED_WAITING') {
        return "label-warning";
    } else if (threadState == 'BLOCKED') {
        return "label-danger";
    }
  };
  $scope.reload();
}]);

bennuAdmin.controller('LoggerController', [ '$scope', '$http', function ($scope, $http) {
  $scope.changeLevel = function(logger, level) {
    $http.get('../api/bennu-core/system/logger/' + logger.name + '/' + level).success(function (data) {
      $scope.loggers = data.loggers;
    });
  }
  $http.get('../api/bennu-core/system/logger').success(function (data) {
    $scope.server = data.server;
    $scope.loggers = data.loggers;
  });
}]);

bennuAdmin.controller('DomainBrowserController', [ '$scope', '$http', '$routeParams', '$location', 
                                                   function ($scope, $http, $routeParams, $location) {
  if(!$routeParams.oid) {
    $location.url('domain-browser/1');
    return;
  }
  $scope.oid = $routeParams.oid;
  $scope.reload = function() {
    $location.url('domain-browser/'+$scope.oid);
  }
  $scope.exploreRelation = function(name) {
    $scope.relation = name; $scope.relations = $scope.relError = null; $scope.currentPage = 0;
    $http.get('../api/bennu-core/domain-browser/' + $scope.oid + '/' + name).success(function (data) {
      $scope.relations = chunk(data, 15); $scope.totalEntries = data.length;
    }).error(function (data, code) { $scope.relError = code; });
  }
  $scope.nextPage = function() {
    if($scope.currentPage < $scope.relations.length - 1)
      $scope.currentPage++;
  }
  $scope.previousPage = function() {
    if($scope.currentPage > 0)
      $scope.currentPage--;
  }
  $(".modal-backdrop").remove(); $("body").removeClass('modal-open'); $scope.error = null;
  $http.get('../api/bennu-core/domain-browser/' + $scope.oid).success(function (data) {
    document.title = data.oid + " · Domain Browser";
    $scope.data = data;
  }).error(function (data, code) { $scope.error = code; $scope.id = $scope.oid; });
}]);

bennuAdmin.filter('capitalize', function() {
  return function(input, scope) {
    input = input.toLowerCase();
    return input.substring(0,1).toUpperCase()+input.substring(1);
  }
});