var app = angular.module('bennuAdmin', [
  'ui.router', 'bennuToolkit' , 'ui.codemirror'
]);

// Router

app.config(['$stateProvider', '$urlRouterProvider',
  function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise("/monitoring/metrics");
    $urlRouterProvider.when("/monitoring", "/monitoring/metrics");
    $urlRouterProvider.when("/scheduler", "/scheduler/logs/");

      // IO
      $stateProvider.

      state('io', {
        url: '/io',
        templateUrl: contextPath + '/bennu-admin/template/IO.html',
        controller: 'IOController'
      });

      // Monitoring
      $stateProvider.

      state('monitoring', {
        url: '/monitoring',
        templateUrl: contextPath + '/bennu-admin/template/monitoring/monitoring.html',
        controller: 'MonitoringController'
      }).
      state('monitoring.jmx', {
        url: '/jmx',
        templateUrl: contextPath + '/bennu-admin/template/monitoring/jmx.html',
        controller: 'JMXController'
      }).
      state('monitoring.logging', {
        url: '/logging',
        templateUrl: contextPath + '/bennu-admin/template/monitoring/logging.html',
        controller: 'LoggingController'
      });

      ['metrics', 'modules', 'configuration', 'libraries', 'env'].map(function (val) {
        $stateProvider.state('monitoring.' + val, {
          url: '/' + val,
          templateUrl: contextPath + '/bennu-admin/template/monitoring/' + val + '.html'
        });
      });

      // Domain Browser
      $stateProvider.

      state('domain-browser', {
        url: '/domain-browser',
        controller: 'DomainBrowserController'
      }).
      state('domain-browser-object', {
        url: '/domain-browser/:id',
        templateUrl: contextPath + '/bennu-admin/template/DomainBrowser.html',
        controller: 'DomainBrowserController'
      });

      // Portal
      $stateProvider.

      state('portal-configuration', {
        url: '/portal-configuration',
        templateUrl: contextPath + '/bennu-admin/template/portal/configuration.html',
        controller: 'PortalConfigurationController'
      }).
      state('portal-menu', {
        url: '/portal-configuration/:id',
        templateUrl: contextPath + '/bennu-admin/template/portal/menu.jsp',
        controller: 'MenuController'
      });

      // User Management
      $stateProvider.

      state('user-management', {
        url: '/user-management',
        templateUrl: contextPath + '/bennu-admin/template/users/management.html',
        controller: 'UserManagementController'
      }).
      state('user-groups', {
        url: '/user-management/groups',
        templateUrl: contextPath + '/bennu-admin/template/users/groups.html',
        controller: 'GroupsController'
      }).
      state('user-management-view', {
        url: '/user-management/:id',
        templateUrl: contextPath + '/bennu-admin/template/users/create.html',
        controller: 'UserManagementController'
      });

      // Scheduler
      $stateProvider.
      state('scheduler', {
        url: '/scheduler',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/scheduler.html',
        controller: 'SchedulerController'
      }).
      state('scheduler.tasks', {
        url: '/tasks',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/tasks.html',
        controller: 'TasksController'
      }).
      state('scheduler.schedules', {
        url: '/schedules',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/schedules.html',
        controller: 'SchedulesController'
      }).
      state('scheduler.logs', {
        url: '/logs/:type',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/logs.html',
        controller: 'LogsController'
      }).
      state('scheduler.log-details', {
        url: '/logs/:type/:id',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/log-details.html',
        controller: 'LogDetailsController'
      }).
      state('scheduler.custom-new', {
        url: '/custom/add',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/add-custom-task.html',
        controller: 'CustomTaskController'
      }).
      state('scheduler.future', {
        url: '/future/',
        templateUrl: contextPath + '/bennu-admin/template/scheduler/future.html',
        controller: 'FutureController'
      });
  }]);

app.run(['$rootScope', function ($rootScope) {
  $rootScope.$watch('pageTitle', function (title) {
    if(title) {
      document.title = title;
    }
  });
}]);

// Filters

app.filter('uptime', function () {
  var n = function(n){ return n > 9 ? "" + n: "0" + n; };
  return function(value) { value = value || 0;
    var days = Math.floor(value/(24*60*60)), hours = Math.floor(value/(60*60)) % 24, mins = Math.floor(value/60) % 60;
    return (value >= 86400 ? n(days) + 'd ' : '') + (value >= 3600 ? n(hours) + 'h ' : '') + (value >= 60 ? n(mins) + 'm ' : '') + n(value % 60)+ 's'; };
});

app.filter('capitalize', function() {
  return function(input, scope) {
    input = input.toLowerCase();
    return input.substring(0,1).toUpperCase()+input.substring(1);
  }
});

app.filter('filename', function () {
  return function(fullPath) {
    return (fullPath || '').split('\\').pop().split('/').pop();
  }
});

// Utility methods

function chunk(arr, n) {
    return arr.slice(0,(arr.length+n-1)/n|0).
           map(function(c,i) { return arr.slice(n*i,n*i+n); });
}


var MD5=function(s){function L(k,d){return(k<<d)|(k>>>(32-d))}function K(G,k){var I,d,F,H,x;F=(G&2147483648);H=(k&2147483648);I=(G&1073741824);d=(k&1073741824);x=(G&1073741823)+(k&1073741823);if(I&d){return(x^2147483648^F^H)}if(I|d){if(x&1073741824){return(x^3221225472^F^H)}else{return(x^1073741824^F^H)}}else{return(x^F^H)}}function r(d,F,k){return(d&F)|((~d)&k)}function q(d,F,k){return(d&k)|(F&(~k))}function p(d,F,k){return(d^F^k)}function n(d,F,k){return(F^(d|(~k)))}function u(G,F,aa,Z,k,H,I){G=K(G,K(K(r(F,aa,Z),k),I));return K(L(G,H),F)}function f(G,F,aa,Z,k,H,I){G=K(G,K(K(q(F,aa,Z),k),I));return K(L(G,H),F)}function D(G,F,aa,Z,k,H,I){G=K(G,K(K(p(F,aa,Z),k),I));return K(L(G,H),F)}function t(G,F,aa,Z,k,H,I){G=K(G,K(K(n(F,aa,Z),k),I));return K(L(G,H),F)}function e(G){var Z;var F=G.length;var x=F+8;var k=(x-(x%64))/64;var I=(k+1)*16;var aa=Array(I-1);var d=0;var H=0;while(H<F){Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=(aa[Z]|(G.charCodeAt(H)<<d));H++}Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=aa[Z]|(128<<d);aa[I-2]=F<<3;aa[I-1]=F>>>29;return aa}function B(x){var k="",F="",G,d;for(d=0;d<=3;d++){G=(x>>>(d*8))&255;F="0"+G.toString(16);k=k+F.substr(F.length-2,2)}return k}function J(k){k=k.replace(/rn/g,"n");var d="";for(var F=0;F<k.length;F++){var x=k.charCodeAt(F);if(x<128){d+=String.fromCharCode(x)}else{if((x>127)&&(x<2048)){d+=String.fromCharCode((x>>6)|192);d+=String.fromCharCode((x&63)|128)}else{d+=String.fromCharCode((x>>12)|224);d+=String.fromCharCode(((x>>6)&63)|128);d+=String.fromCharCode((x&63)|128)}}}return d}var C=Array();var P,h,E,v,g,Y,X,W,V;var S=7,Q=12,N=17,M=22;var A=5,z=9,y=14,w=20;var o=4,m=11,l=16,j=23;var U=6,T=10,R=15,O=21;s=J(s);C=e(s);Y=1732584193;X=4023233417;W=2562383102;V=271733878;for(P=0;P<C.length;P+=16){h=Y;E=X;v=W;g=V;Y=u(Y,X,W,V,C[P+0],S,3614090360);V=u(V,Y,X,W,C[P+1],Q,3905402710);W=u(W,V,Y,X,C[P+2],N,606105819);X=u(X,W,V,Y,C[P+3],M,3250441966);Y=u(Y,X,W,V,C[P+4],S,4118548399);V=u(V,Y,X,W,C[P+5],Q,1200080426);W=u(W,V,Y,X,C[P+6],N,2821735955);X=u(X,W,V,Y,C[P+7],M,4249261313);Y=u(Y,X,W,V,C[P+8],S,1770035416);V=u(V,Y,X,W,C[P+9],Q,2336552879);W=u(W,V,Y,X,C[P+10],N,4294925233);X=u(X,W,V,Y,C[P+11],M,2304563134);Y=u(Y,X,W,V,C[P+12],S,1804603682);V=u(V,Y,X,W,C[P+13],Q,4254626195);W=u(W,V,Y,X,C[P+14],N,2792965006);X=u(X,W,V,Y,C[P+15],M,1236535329);Y=f(Y,X,W,V,C[P+1],A,4129170786);V=f(V,Y,X,W,C[P+6],z,3225465664);W=f(W,V,Y,X,C[P+11],y,643717713);X=f(X,W,V,Y,C[P+0],w,3921069994);Y=f(Y,X,W,V,C[P+5],A,3593408605);V=f(V,Y,X,W,C[P+10],z,38016083);W=f(W,V,Y,X,C[P+15],y,3634488961);X=f(X,W,V,Y,C[P+4],w,3889429448);Y=f(Y,X,W,V,C[P+9],A,568446438);V=f(V,Y,X,W,C[P+14],z,3275163606);W=f(W,V,Y,X,C[P+3],y,4107603335);X=f(X,W,V,Y,C[P+8],w,1163531501);Y=f(Y,X,W,V,C[P+13],A,2850285829);V=f(V,Y,X,W,C[P+2],z,4243563512);W=f(W,V,Y,X,C[P+7],y,1735328473);X=f(X,W,V,Y,C[P+12],w,2368359562);Y=D(Y,X,W,V,C[P+5],o,4294588738);V=D(V,Y,X,W,C[P+8],m,2272392833);W=D(W,V,Y,X,C[P+11],l,1839030562);X=D(X,W,V,Y,C[P+14],j,4259657740);Y=D(Y,X,W,V,C[P+1],o,2763975236);V=D(V,Y,X,W,C[P+4],m,1272893353);W=D(W,V,Y,X,C[P+7],l,4139469664);X=D(X,W,V,Y,C[P+10],j,3200236656);Y=D(Y,X,W,V,C[P+13],o,681279174);V=D(V,Y,X,W,C[P+0],m,3936430074);W=D(W,V,Y,X,C[P+3],l,3572445317);X=D(X,W,V,Y,C[P+6],j,76029189);Y=D(Y,X,W,V,C[P+9],o,3654602809);V=D(V,Y,X,W,C[P+12],m,3873151461);W=D(W,V,Y,X,C[P+15],l,530742520);X=D(X,W,V,Y,C[P+2],j,3299628645);Y=t(Y,X,W,V,C[P+0],U,4096336452);V=t(V,Y,X,W,C[P+7],T,1126891415);W=t(W,V,Y,X,C[P+14],R,2878612391);X=t(X,W,V,Y,C[P+5],O,4237533241);Y=t(Y,X,W,V,C[P+12],U,1700485571);V=t(V,Y,X,W,C[P+3],T,2399980690);W=t(W,V,Y,X,C[P+10],R,4293915773);X=t(X,W,V,Y,C[P+1],O,2240044497);Y=t(Y,X,W,V,C[P+8],U,1873313359);V=t(V,Y,X,W,C[P+15],T,4264355552);W=t(W,V,Y,X,C[P+6],R,2734768916);X=t(X,W,V,Y,C[P+13],O,1309151649);Y=t(Y,X,W,V,C[P+4],U,4149444226);V=t(V,Y,X,W,C[P+11],T,3174756917);W=t(W,V,Y,X,C[P+2],R,718787259);X=t(X,W,V,Y,C[P+9],O,3951481745);Y=K(Y,h);X=K(X,E);W=K(W,v);V=K(V,g)}var i=B(Y)+B(X)+B(W)+B(V);return i.toLowerCase()};


// Controllers

app.controller('IOController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
  $scope.$root.pageTitle = 'Bennu IO';
  $http.get(contextPath + '/api/bennu-io/storage').success(function (data) {
    $scope.storages = data.storages;
  });
  $http.get(contextPath + '/api/bennu-io/storage/count').success(function (data) {
    $scope.counts = data;
  });
  $http.get(contextPath + '/api/bennu-io/storage/config').success(function (configs) {
    $scope.configs = configs.storageConfigurations;
  });
  $scope['delete'] = function(storage) {
    $http['delete'](contextPath + '/api/bennu-io/storage/' + storage.id).success(function () {
      $scope.storages.splice($scope.storages.indexOf(storage), 1);
    }).error(function (data) { $scope.error = data; });
  }
  $scope.newDomainStorage = function() {
    $http.post(contextPath + '/api/bennu-io/storage/domain/' + $scope.domainName).success(function (data) {
      $scope.domainName = null; $scope.storages.push(data); $scope.counts[data.id] = 0;
    }).error(function (data) { $scope.error = data; });
    $('#newStorageModal').modal('hide');
  }
  $scope.newFSStorage = function() {
    $http.post(contextPath + '/api/bennu-io/storage/lfs', 
              {'name' : $scope.fsName, 'path': $scope.path, 'treeDirectoriesNameLength': $scope.treeLen}).
              success(function (data) {
                $scope.fsName = $scope.path = $scope.treeLen = null; $scope.storages.push(data);
                $scope.counts[data.id] = 0;
              }).error(function (data) { $scope.error = data; });
    $('#newStorageModal').modal('hide');
  }
  $scope.setDefault = function(storage) {
    $http.post(contextPath + '/api/bennu-io/storage/default/' + storage.id).success(function (data) {
      $scope.storages = data.storages;
    }).error(function (data) { $scope.error = data; });
  }
  $scope.save = function(config) {
    var configs = Array({ 'fileStorageConfigurationId': config.id, 'fileStorageId': config.storage.id });
    $http.post(contextPath + '/api/bennu-io/storage/config', configs).success(function (data) {
      config.success = true;
      $timeout(function () {
        config.success = false;
      }, 2000);
    });
  }
}]);

app.threadMap = {'RUNNABLE': 'success', 'WAITING': 'info', 'TIMED_WAITING': 'warning', 'BLOCKED': 'danger'};
app.loggerMap = [['TRACE', 'active btn-danger'],
                 ['DEBUG', 'active btn-warning'],
                 ['INFO', 'active btn-info'],
                 ['WARN', 'active btn-success'],
                 ['ERROR', 'active btn-primary'],
                 ['OFF', 'active btn-info']];

app.controller('JMXController', ['$scope', '$http', function ($scope, $http) {
  $http.get(contextPath + '/api/bennu-core/system/jmx').success(function (data) { $scope.jmxData = data; });
}]);

app.controller('LoggingController', ['$scope', '$http', function ($scope, $http) {
  $scope.changeLevel = function(logger, level) {
    $http.post(contextPath + '/api/bennu-core/system/logger/' + logger.name + '/' + level).success(function (data) {
      $scope.loggers = data.loggers;
    });
  }
  $http.get(contextPath + '/api/bennu-core/system/logger').success(function (data) {
    $scope.loggers = data.loggers;
  });
  $scope.loggerMap = app.loggerMap;
}]);

app.controller('MonitoringController', [ '$scope', '$http', function ($scope, $http) {
  $scope.$root.pageTitle = 'System Monitoring';
  $scope.reload = function(full) {
    $http.get(contextPath + '/api/bennu-core/system/info?full=' + full).success(function (data) {
      if(full) { $scope.data = data; } else { $scope.data.metrics = data.metrics; }
    });
  }
  $scope.threadDump = function() {
    $http.get(contextPath + '/api/bennu-core/system/thread-dump').success(function (data) {
      $scope.threads = data;
    });
  }
  $scope.healthcheck = function() {
    $http.get(contextPath + '/api/bennu-core/system/healthcheck').success(function (data) {
      $scope.healthchecks = data; $scope.currentCheck = null;
    });
  }
  $scope.fullGC = function() {
    $http.put(contextPath + '/api/bennu-core/system/full-gc').success(function (data) {
      $scope.data.metrics = data.metrics;
    });
  }
  $scope.setCheck = function(check) { $scope.currentCheck = check; };
  $scope.getLabelClass = function(threadState) {
    return 'label-' + app.threadMap[threadState];
  };
  $scope.hideProperty = function(key) {
    key = key.toLowerCase();
    return key.indexOf('pass') > -1 || key.indexOf('secret') > -1 || key.indexOf('private') > -1 || key.indexOf('key') > -1;
  };
  $scope.reload(true);
}]);

app.controller('DomainBrowserController', [ '$scope', '$http', '$state',
                                                   function ($scope, $http, $state) {
  if(!$state.params.id) {
    $state.go('domain-browser-object', {'id': 1});
    return;
  }
  $scope.oid = $state.params.id;
  $scope.$root.pageTitle = $scope.oid + ' - System Monitoring';
  $scope.reload = function() { $state.go('domain-browser-object', {id: $scope.oid}); };
  $scope.exploreRelation = function(name) {
    $scope.relation = name; $scope.relations = $scope.relError = null; $scope.currentPage = 0;
    $http.get(contextPath + '/api/bennu-core/domain-browser/' + $scope.oid + '/' + name).success(function (data) {
      $scope.relations = chunk(data, 15); $scope.totalEntries = data.length;
    }).error(function (data, code) { $scope.relError = code; });
  }
  $scope.nextPage = function() {
    if($scope.currentPage < $scope.relations.length - 1)
      $scope.currentPage++;
  }
  $scope.relationCounts = {};
  $scope.loadRelationCount = function(name) {
    $scope.relationCounts[name] = -1;
    $http.get(contextPath + '/api/bennu-core/domain-browser/' + $scope.oid + '/' + name + '/count').success(function (data) {
      $scope.relationCounts[data.name] = data.size;
    }).error(function (data, code) { $scope.relError = code; });
  }
  $scope.previousPage = function() {
    if($scope.currentPage > 0)
      $scope.currentPage--;
  }
  $(".modal-backdrop").remove(); $("body").removeClass('modal-open'); $scope.error = null;
  $http.get(contextPath + '/api/bennu-core/domain-browser/' + $scope.oid).success(function (data) {
    $scope.data = data;
  }).error(function (data, code) { $scope.error = code; $scope.id = $scope.oid; });
}]);

app.controller('PortalConfigurationController', ['$scope', '$http', function ($scope, $http) {
  $scope.$root.pageTitle = 'Portal Configuration';
  $scope.logoUrl = contextPath + '/api/bennu-portal/configuration/logo?' + new Date().getTime();
  $scope.faviconUrl = contextPath + '/api/bennu-portal/configuration/favicon?' + new Date().getTime();
  $http.get(contextPath + '/api/bennu-portal/configuration').success(function (data) {
    $scope.menu = data;
  });
  $scope.error = '';
  $scope.save = function() {
    $http.put(contextPath + '/api/bennu-portal/configuration/' + $scope.menu.id, $scope.menu).success(function () {
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
                      $scope.logoUrl = 'data:' + file.type + ';base64,' + picBase64;
                      $scope.menu.logo = picBase64;
                      $scope.menu.logoType = file.type;
                    });
              }
              if(type === "favicon"){
                $scope.$apply(function () {
                      $scope.faviconUrl = 'data:' + file.type + ';base64,' + picBase64;
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

function i18n(input) {
  return Bennu.localizedString.getContent(input, Bennu.locale);
}

app.controller('MenuController', [ '$scope', '$state', '$http', function($scope, $state, $http) {
  $scope.$root.pageTitle = 'Portal Configuration';
  $scope.id = $state.params.id;
  $scope.handleError = function(data) { $scope.error = data; $scope.saving = false; };
  $scope.deleteSelected = function() {
    if($scope.selected.id) {
      $http['delete'](contextPath + "/api/bennu-portal/menu/" + $scope.selected.id).success(function (data) {
        $scope.selected.node.remove(); $scope.selected = null;
      }).error($scope.handleError);
    } else {
      $scope.selected.node.remove(); $scope.selected = null;
    }
  }
  $scope.saveSelected = function() {
    var data = { title: $scope.selected.title, description: $scope.selected.description, visible: $scope.selected.visible,
                 layout: $scope.selected.layout, accessExpression: $scope.selected.accessExpression, icon: $scope.selected.icon ,
                 documentationUrl: $scope.selected.documentationUrl, supportConfig: $scope.selected.supportConfig.id, faqUrl: $scope.selected.faqUrl };
    var promise;
    if($scope.selected.id) {
      promise = $http.put(contextPath + "/api/bennu-portal/menu/" + $scope.selected.id, data);
    } else if ($scope.selected.subRoot) {
      data.key = $scope.selected.path;
      promise = $http.post(contextPath + "/api/bennu-portal/menu/sub-root", data);
    } else {
      data.path = $scope.selected.path; data.parent = $scope.selected.parent;
      promise = $http.post(contextPath + "/api/bennu-portal/menu", data);
    }
    $scope.error = null; $scope.saving = true;
    promise.success(function (data) {
      var node = $scope.selected.node;
      $scope.selected = data; $scope.saving = false;
      data.node = node;
      node.setTitle(i18n(data.title));
      $scope.reload(node);
    }).error($scope.handleError);

  }
  $scope.loadApps = function() {
    $scope.filter = null;
    if(!$scope.apps) {
      $http.get(contextPath + "/api/bennu-portal/menu/applications/").success(function (data) {
        $scope.apps = data;
      });
    }
  }
  $scope.installApplication = function() {
    $http.post(contextPath + "/api/bennu-portal/menu/applications/", { root: $scope.selected.id, key: $scope.newApp.key }).success(function (item) {
      add(item, $scope.selected.node);
    }).error($scope.handleError);
  }
  $scope.installFunctionality = function() {
    $http.post(contextPath + '/api/bennu-portal/menu/functionalities/', 
              { root: $scope.selected.id, appKey: $scope.newApp.key, provider: $scope.newFunctionality.provider, key: $scope.newFunctionality.key}).
              success(function (item) {
      add(item, $scope.selected.node);
    }).error($scope.handleError);
  }
  $scope.createChild = function(root) {
    var mls = {}; 
    mls[Bennu.locale.tag] = root ? 'New Sub-Root' : 'New Entry';
    var newChild = { title: mls, description: {}, visible: true, accessExpression: 'anyone', functionality: false, parent: $scope.selected.id, subRoot: root, supportConfig: $("#tree").fancytree("getTree").getFirstChild().data.item.supportConfig, supportConfigs: $("#tree").fancytree("getTree").getFirstChild().data.item.supportConfigs};
    add(newChild, $scope.selected.node).setActive(true);
  }
  $scope.addSupport = function() {
    $http.post(contextPath + '/api/bennu-portal/menu/' + $scope.selected.id + '/support/',
                {title : $scope.newSupportTitle, email : $scope.newSupportEmail}).
                success(function (data) {
            var node = $scope.selected.node;
            $scope.selected = data;
            data.node = node;
            node.setTitle(i18n(data.title));
            $scope.reload(node);
    }).error($scope.handleError);
  }
  $scope.saveOrder = function() {
    $http.post(contextPath + "/api/bennu-portal/menu/order", $scope.changes).success(function () {
      $scope.changes = null;
    }).error($scope.handleError);
  }

  var add = function(item, parent) {
    var node = parent.addChildren({ title: (item.subRoot? '[MENU] ' : '') + i18n(item.title), key: item.id, folder: !item.functionality, item: item });
    item.node = node;
    if(item.menu) {
      item.menu.forEach(function (entry) {
        add(entry, node);
      });
    }
    return node;
  }

  $scope.reload = function(expandedNode) {
      $http.get(contextPath + '/api/bennu-portal/menu/' + $scope.id).success(function (data) {
          $scope.locales = Bennu.locales;
          $("#tree").fancytree({ source: [ ], extensions: ["dnd"],
            dnd: {
              preventRecursiveMoves: true,
              dragStart: function(node, data) { return true; },
              dragEnter: function(node, data) {
                if(node.parent !== data.otherNode.parent) return false;
                return ["before", "after"];
              },
              dragDrop: function(node, data) {
                $scope.changes = $scope.changes || {};
                data.otherNode.moveTo(node, data.hitMode);
                node.parent.children.forEach(function (node) {
                  $scope.changes[node.key] = node.getIndex();
                });
                $scope.$apply();
              }
            }
           });
          var tree = $("#tree").fancytree("getTree");
          $("#tree").bind("fancytreeactivate", function(event, data) {
            $scope.selected = data.node.data.item; $scope.error = null;
            if(!$scope.$$phase) {
              $scope.$apply();
            }
          });
          add(data, tree.rootNode);
          if(!expandedNode) {
            tree.rootNode.children[0].setActive(true);
          } else {
            var node = tree.getNodeByKey(expandedNode.key);
            node.setActive(true);
          }


          $("#tree ul").focus();
        });
  };

  $scope.reload();

}]);


app.controller('UserManagementController', [ '$scope', '$http', '$state', '$location',
                                                   function ($scope, $http, $state, $location) {
  $scope.$root.pageTitle = 'User Management';
  $scope.updateDisplayName = function(user) {
    var first = user.givenNames ? user.givenNames.split(' ')[0] : "";
    var last = user.familyNames ? user.familyNames.split(' ')[user.familyNames.split(' ').length - 1] : "";
    user.displayName = (first + " " + last).trim();
  };

  $scope.updateGravatar = function() {
    if ($scope.avatarchoice == 'gravatar' && $scope.user.email) {
      $('#img-gravatar').fadeOut(function() {
        $('#img-gravatar')[0].onload = function() {
          $('#img-gravatar').fadeIn();
        }
        $('#img-gravatar').attr('src', 'https://secure.gravatar.com/avatar/' + MD5($scope.user.email) + '?d=mm&s=300');
      });
    }
  };

  $scope.fileNameChanged = function(e) {
    var files = e.files; // FileList object
    for ( var i = 0; i < files.length; i++) {
      var file = files[i];
      if (!file.type.match("image.*")) {
        $scope.errors = "<p>Apenas são aceites imagens</p>";
        continue;
      }
      if (file.size > 1024 * 1024) { // 1M
        $scope.errors = "<p>Imagem muito grande. Tamanho máximo : 1M</p>";
        continue;
      }
      var reader = new FileReader();
      reader.onload = function(e) {
        $('#cropzone').empty();
        var img = new Image();
        var content = e.target.result;
        img.onload = function() {
          var size = Math.min(img.width, img.height) * .9;
          $(img).cropper({
            aspeatio: 1,
            data: { x1: size * .05, y1: size * .05, width: size, heigth: size},
            done: function(data) {
              $scope.user.avatar = {
                img: content.substr(content.indexOf(",") + 1, content.length),
                mimeType: file.type,
                x1: data.x1,
                y1: data.y1,
                x2: data.x2,
                y2: data.y2
              };
            }
          });
        }
        $('#cropzone').append(img);
        img.src = content;
      }
      reader.readAsDataURL(file);
    }
  };

  $scope.save = function(user) {
    $scope.errors = null; $scope.success = false;
    if($scope.newUser) {
      $http.post(contextPath + '/api/bennu-core/users', user).success(function (data) {
        $location.url('user-management/' + data.username);
      }).error(function(data, code) {
        $scope.errors = data.message;
      });
    } else {
      $http.put(contextPath + '/api/bennu-core/users/' + user.username, user).success(function (data) {
        $scope.user = data; $scope.success = true;
      }).error(function(data, code) {
        $scope.errors = data.message;
      });
    }
  };

  $scope.reset = function() {
    $scope.avatarchoice = 'upload';
    $scope.user = { preferredLocale: $scope.locales[0] };
  };

  $scope.locales = Bennu.locales;

  if ($state.params.id) {
    if($state.params.id !== 'create') {
      $scope.newUser = false; $scope.avatarchoice = 'custom';
      $http.get(contextPath + '/api/bennu-core/users/username/' + $state.params.id).success(function (data) {
        $scope.user = data;
      });
    } else {
      $scope.newUser = true;
      $scope.reset();
    }
  }

  $scope.users = [];

  $scope.doSearch = function() {
    $scope.currentSearch = $scope.query;
    if($scope.query) {
      $http.post(contextPath + '/api/bennu-core/users/find?includeInactive=true&query=' + $scope.query).success(function (data) {
        $scope.users = data.users;
      });   
    } else {
      $scope.users = [];
    }
  };
  $http.get(contextPath + '/api/bennu-core/users/data').success(function (data) {
    $scope.userData = data;
  });
}]);

app.controller('GroupsController', ['$scope', '$http', function($scope, $http) {
  $scope.$root.pageTitle = 'Dynamic Group Management';
  $http.get(contextPath + '/api/bennu-core/groups/dynamic').success(function (data) {
    $scope.groups = data;
  });
  function doIt(op, user) {
    return $http.post(contextPath + '/api/bennu-core/groups/dynamic/' + op, {'group': $scope.selectedGroup.name, 'user': user.username}).success(function (data) {
      $scope.groups[$scope.groups.indexOf($scope.selectedGroup)] = data; $scope.selectedGroup = data; $scope.newUser = {};
    });
  }
  $scope.grant = function(user) { doIt('grant', user).success(function() { $scope.error = false; }).error(function() { $scope.error = true; }); };
  $scope.revoke = function(user) { doIt('revoke', user); };
  $scope.saveName = function() { $http.post(contextPath + '/api/bennu-core/groups/dynamic',
      {'group': $scope.selectedGroup.name, 'name': $scope.selectedGroup.customPresentationName}).success(function (data) {
        $scope.groups[$scope.groups.indexOf($scope.selectedGroup)] = data; $scope.selectedGroup = data;
    });};
  $scope.min = Math.min;
}]);

// Scheduler

app.controller('SchedulerController', ['$scope', '$http', '$timeout', function ($scope, $http, $timeout) {
  $scope.$root.pageTitle = undefined;
  $scope.contextPath = Bennu.contextPath;
  $http.get(contextPath + '/api/bennu-scheduler/config').success(function (data) {
    $scope.config = data;
  });
  $scope.saveStore = function() {
    $http.put(contextPath + '/api/bennu-scheduler/config/' + $scope.config.loggingStorage.id).success(function (data) {
      $scope.config = data; $scope.storeSuccess = true;
      $timeout(function () {
        $scope.storeSuccess = false;
      }, 2000);
    });
  };
  $scope.clear = function() {
    if(confirm('Are you sure you wish to clear all schedules?')) {
      $http['delete'](contextPath + '/api/bennu-scheduler/schedule').success(function () {
        location.reload();
      });
    }
  };
  $scope.load = function() {
    $("#input-load-dump-file").on('change', function (e) {
      e.preventDefault();
      var file = e.target.files[0];
      var reader = new FileReader();
      reader.onload = function() {
        $http.post(contextPath + '/api/bennu-scheduler/schedule/load-dump', reader.result).success(function (data) {
          location.reload();
        });
      };
      reader.readAsText(file);
    });
    $("#input-load-dump-file").click();
  };
}]);

app.controller('TasksController', ['$scope', '$http', '$state', '$timeout', function ($scope, $http, $state, $timeout) {
  $http.get(contextPath + '/api/bennu-scheduler/tasks').success(function (data) {
    $scope.tasks = data.tasks;
  });
  $scope.addSchedule = function() {
    $('#newScheduleModal').modal('hide');
    $http.post(contextPath + '/api/bennu-scheduler/schedule', {'type': $scope.type, 'schedule': $scope.schedule}).success(function () {
      $timeout(function () {
        $state.go('scheduler.schedules');
      }, 300);
    });
  }
  $scope.runNow = function(task) {
    $http.post(contextPath + '/api/bennu-scheduler/tasks/' + task.type).success(function () {
      $state.go('scheduler.logs');
    });
  }
}]);

app.controller('SchedulesController', ['$scope', '$http', function ($scope, $http) {
  $http.get(contextPath + '/api/bennu-scheduler/schedule').success(function (data) {
    $scope.schedules = data.schedule;
  });
  $scope['delete'] = function(schedule) {
    $http['delete'](contextPath + '/api/bennu-scheduler/schedule/' + schedule.id).success(function () {
      $scope.schedules.splice($scope.schedules.indexOf(schedule), 1);
    });
  }
}]);

app.controller('LogsController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
  $scope.type = $state.params.type;
  $scope.numResults = 20;
  $scope.customTaskOnly = false;
  var url = $state.params.type ? contextPath + '/api/bennu-scheduler/log/' + $scope.type : contextPath + '/api/bennu-scheduler/log/';
  $scope.reload = function () {
    $http.get(url + '?count=' + $scope.numResults).success(function (data) {
      $scope.logs = data; $scope.moreResults = data.length == $scope.numResults;
    });
  };
  $scope.loadMore = function() {
    $http.get(url + '?count=' + ($scope.numResults + 1) + '&start=' + $scope.logs[$scope.logs.length -1].id).success(function(data) {
      data.shift();
      $scope.logs = $scope.logs.concat(data); $scope.moreResults = data.length == $scope.numResults;
    });
  };
  $scope.filterCustom = function(log) {
    return $scope.customTaskOnly === false || log.custom === true;
  };
  $scope.reload();
}]);

app.controller('LogDetailsController', ['$scope', '$http', '$state', '$interval', function ($scope, $http, $state, $interval) {
  $scope.type = $state.params.type; $scope.id = $state.params.id; $scope.contextPath = contextPath;
  $scope.editorOptions = { lineNumbers: true, mode: 'text/x-java', theme: 'eclipse', readOnly: true};
  $scope.showCode = true;
  $scope.refreshLog = function() {
    $http.get(contextPath + '/api/bennu-scheduler/log/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.log = data;
    });
    $http.get(contextPath + '/api/bennu-scheduler/log/cat/' + $scope.type + '/' + $scope.id).success(function (data) {
      $scope.logs = data;
      $scope.threadTask();
    });
  };
  $scope.killTask = function () {
    if (confirm('Are you sure you want to kill this task?')) {
      $http.get(contextPath + '/api/bennu-scheduler/log/kill/' + $scope.type).success(function (data) {
        $scope.refreshLog();
      });
    }
  };
  $scope.threadTask = function () {
    $http.get(contextPath + '/api/bennu-scheduler/log/thread/' + $scope.type).success(function (data) {
        $scope.thread = data;
    });
  };
  $scope.load = function() {
    window.code = $scope.log.code; $state.go('scheduler.custom-new');
  };
  var promise = $interval(function () {
    if($scope.log && !$scope.log.end) {
      $scope.refreshLog();
    }
  }, 5000);
  $scope.$on('$destroy', function() {
    if (angular.isDefined(promise)) {
      $interval.cancel(promise);
      promise = undefined;
    }
  });
  $scope.codemirrorLoaded = function(_editor) { setTimeout(function() { _editor.refresh();}, 200); }
  $scope.refreshLog();
}]);

app.controller('CustomTaskController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
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
    $http.put(contextPath + '/api/bennu-scheduler/custom/compile', {'name': fqn, 'code': $scope.javaCode}).success(function (result) {
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
    $http.put(contextPath + '/api/bennu-scheduler/custom', {'name': fqn, 'code': $scope.javaCode}).success(function (result) { $state.go('scheduler.logs') });
  }
  $scope.$watch('javaCode', function (oldValue, newValue) {
    $scope.compiled = false; $scope.result = '';
  });
}]);

app.controller('CustomTaskLogsController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
  $scope.type = $state.params.type;
  var url = $state.params.type ? contextPath + '/api/bennu-scheduler/custom/' + $scope.type : contextPath + '/api/bennu-scheduler/custom/';
  $scope.reload = function () {
    $http.get(url).success(function (data) {
      $scope.logs = data.logs;
      angular.forEach($scope.logs, function(value) { value.date = moment(value.begin).format("DD MMMM YYYY HH:mm:ss");  });
    });
  }
  $scope.reload();
}]);

app.controller('FutureController', ['$scope', '$http', '$state', function ($scope, $http, $state) {
  $scope.numResults = 20;
  $scope.totalNumResults = 0;
  var url = contextPath + '/api/bennu-scheduler/future/';
  $scope.reload = function () {
    $http.get(url + '?count=' + $scope.numResults).success(function (data) {
      $scope.futureTasks = data;
      $scope.moreResults = data.length == $scope.numResults;
      $scope.totalNumResults = data.length;
    });
  }
  $scope.loadMore = function() {
    $http.get(url + '?count=' + ($scope.numResults) + '&skip=' + ($scope.totalNumResults)).success(function(data) {
      $scope.futureTasks = $scope.futureTasks.concat(data);
      $scope.moreResults = data.length == $scope.numResults;
      $scope.totalNumResults += data.length;
    });
  };
  $scope.cancelFuture = function(futureId) {
    if (confirm('Are you sure you want to cancel this future task?')) {
      $http.get(url + 'cancel/' + futureId).success(function(data) {
        $http.get(url + '?count=' + ($scope.totalNumResults)).success(function(data) {
          $scope.futureTasks = data;
        });
      });
    }
  };
  $scope.reload();
}]);
