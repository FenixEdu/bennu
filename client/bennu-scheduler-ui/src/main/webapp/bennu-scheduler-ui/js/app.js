var bennuScheduler = angular.module('bennuScheduler', [
  'ngRoute', 'ui.codemirror', 'bennuToolkit'
]);

bennuScheduler.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/tasks', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/Tasks.html',
        controller: 'TasksCtrl'
      }).
      when('/schedule/:type', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/ScheduleCreate.html',
        controller: 'ScheduleCtrl'
      }).
      when('/schedules', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/Schedules.html',
        controller: 'SchedulesCtrl'
      }).
      when('/logs/:type?', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/Logs.html',
        controller: 'LogsCtrl'
      }).
      when('/logs/:type/:id', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/LogDetails.html',
        controller: 'LogDetailsCtrl'
      }).
      when('/custom/add', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/AddCustomTask.html',
        controller: 'CustomTaskCtrl'
      }).
      when('/custom/:type?', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/CustomLogs.html',
        controller: 'CustomTaskLogsCtrl'
      }).
      when('/custom/:type/:id', {
        templateUrl: contextPath + '/bennu-scheduler-ui/template/LogDetails.html',
        controller: 'CustomLogDetailsCtrl'
      }).
      otherwise({
        redirectTo: '/tasks'
      });
  }]);

bennuScheduler.controller('TasksCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
  $http.get(contextPath + '/api/bennu-scheduler/tasks').success(function (data) {
    $scope.tasks = data.tasks;
  });
  $scope.runNow = function(task) {
    $http.post(contextPath + '/api/bennu-scheduler/tasks/' + task.type).success(function () {
      $location.url('/logs');
    });
  }
}]);

bennuScheduler.controller('ScheduleCtrl', ['$scope', '$http', '$routeParams', '$location', function ($scope, $http, $routeParams, $location) {
  $scope.type = $routeParams.type;
  $scope.add = function() {
    $http.post(contextPath + '/api/bennu-scheduler/schedule', {'type': $scope.type, 'schedule': $scope.schedule}).success(function () {
      $location.url('schedules');
    });
  }
}]);

bennuScheduler.controller('SchedulesCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
  $http.get(contextPath + '/api/bennu-scheduler/schedule').success(function (data) {
    $scope.schedules = data.schedule;
  });
  $http.get(contextPath + '/api/bennu-scheduler/config').success(function (data) {
    $scope.config = data;
  });
  $scope.contextPath = window.contextPath;
  $scope.saveStore = function() {
    $scope.saving = true;
    $http.put(contextPath + '/api/bennu-scheduler/config/' + $scope.config.loggingStorage.id).success(function (data) {
      $scope.config = data; $scope.saving = false;
    });
  }
  $scope.delete = function(schedule) {
    $http.delete(contextPath + '/api/bennu-scheduler/schedule/' + schedule.id).success(function () {
      $scope.schedules.splice($scope.schedules.indexOf(schedule), 1);
    });
  }
  $scope.clear = function() {
    if(confirm('Are you sure you wish to clear all schedules?')) {
      $http.delete(contextPath + '/api/bennu-scheduler/schedule').success(function () {
        $scope.schedules = new Array();
      });
    }
  }
  $scope.load = function() {
    $("#input-load-dump-file").on('change', function (e) {
      e.preventDefault();
      var file = e.target.files[0];
      var reader = new FileReader();
      reader.onload = function() {
        $http.post(contextPath + '/api/bennu-scheduler/schedule/load-dump', reader.result).success(function (data) {
         $scope.schedules = data.schedule;
        });
      };
      reader.readAsText(file);
    });
    $("#input-load-dump-file").click();
  }
}]);

bennuScheduler.controller('LogsCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
  $scope.type = $routeParams.type;
  var url = $routeParams.type ? contextPath + '/api/bennu-scheduler/log/' + $scope.type : contextPath + '/api/bennu-scheduler/log/';
  $scope.reload = function () {
    $http.get(url).success(function (data) {
      $scope.logs = data.logs;
      angular.forEach($scope.logs, function(value) { value.date = moment(value.begin).format("DD MMMM YYYY HH:mm:ss");  });
    });
  }
  $scope.reload();
}]);

bennuScheduler.controller('LogDetailsCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
  $scope.type = $routeParams.type; $scope.id = $routeParams.id; $scope.contextPath = contextPath;
  $scope.refreshLog = function() {
    $http.get(contextPath + '/api/bennu-scheduler/log/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.log = data;
    });
    $http.get(contextPath + '/api/bennu-scheduler/log/cat/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.logs = data;
    });
  }
  setInterval(function () {
    if($scope.log && !$scope.log.end) {
      $scope.refreshLog();
    }
  }, 3000);
  $scope.refreshLog();
}]);

bennuScheduler.controller('CustomLogDetailsCtrl', ['$scope', '$http', '$routeParams', '$location', function ($scope, $http, $routeParams, $location) {
  $scope.type = $routeParams.type; $scope.id = $routeParams.id; $scope.contextPath = contextPath;
  $scope.editorOptions = { lineNumbers: true, mode: 'text/x-java', theme: 'eclipse', readOnly: true};
  $scope.refreshLog = function() {
    $http.get(contextPath + '/api/bennu-scheduler/custom/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.log = data;
    });
    $http.get(contextPath + '/api/bennu-scheduler/custom/cat/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.logs = data;
    });
  }
  $scope.load = function() {
    window.code = $scope.log.javaCode; $location.url('/custom/add');
  }
  setInterval(function () {
    if($scope.log && !$scope.log.end) {
      $scope.refreshLog();
    }
  }, 3000);
  $scope.codemirrorLoaded = function(_editor) { setTimeout(function() { _editor.refresh();}, 200); }
  $scope.refreshLog();
}]);

bennuScheduler.controller('CustomTaskCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
  $scope.javaCode = window.code || ""; window.code = null;
  $scope.compiled = false;   $scope.editorOptions = { lineNumbers: true, mode: 'text/x-java', theme: 'eclipse'};

  $scope.compile = function() {
    var packageRegex = new RegExp("package (.*);").exec($scope.javaCode);
    var classNameRegex = new RegExp("public class (.*) extends").exec($scope.javaCode);
    if (packageRegex == null) {
      $scope.result = 'Error: Custom Tasks cannot be defined in the default package!';
      return;
    }
    if (classNameRegex == null) {
      $scope.result = 'Error: Cannot determine class name. Are you sure your class is public and extends CustomTask?';
      return;
    }
    var fqn = packageRegex[1] + "." + classNameRegex[1];
    $http({ method: 'POST', url: contextPath + '/api/bennu-scheduler/custom/compile', data: {name: fqn, code: $scope.javaCode},
          headers: {'Content-Type': 'application/json'}}).success(function (result) {
      if(result.compileOK) {
        $scope.result = 'The compilation was successful!'; $scope.compiled = true;
      } else {
        $scope.result = result.error;
      }
    });
  }
  $scope.run = function() {
    var packageName = new RegExp("package (.*);").exec($scope.javaCode)[1];
    var className = new RegExp("public class (.*) extends").exec($scope.javaCode)[1];
    var fqn = packageName + "." + className;
    $http({ method: 'POST', url: contextPath + '/api/bennu-scheduler/custom', data: {name: fqn, code: $scope.javaCode},
          headers: {'Content-Type': 'application/json'}}).success(function (result) { $location.url('custom') });
  }
  $scope.$watch('javaCode', function (oldValue, newValue) {
    $scope.compiled = false; $scope.result = '';
  });
}]);

bennuScheduler.controller('CustomTaskLogsCtrl', ['$scope', '$http', '$routeParams', function ($scope, $http, $routeParams) {
  $scope.type = $routeParams.type;
  var url = $routeParams.type ? contextPath + '/api/bennu-scheduler/custom/' + $scope.type : contextPath + '/api/bennu-scheduler/custom/';
  $scope.reload = function () {
    $http.get(url).success(function (data) {
      $scope.logs = data.logs;
      angular.forEach($scope.logs, function(value) { value.date = moment(value.begin).format("DD MMMM YYYY HH:mm:ss");  });
    });
  }
  $scope.reload();
}]);

bennuScheduler.filter('moment', function () {
    return function (text) {
        return text ? moment(text).format("DD MMMM YYYY HH:mm:ss") : 'N/A';
    };
});