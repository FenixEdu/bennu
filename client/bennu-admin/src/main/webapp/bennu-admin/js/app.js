if(!window.functionality) { window.location = contextPath + (BennuPortal.username ? '' : '/login?callback=' + location.href); }

var bennuAdmin = angular.module('bennuAdmin', [
  'ngRoute'
]);

bennuAdmin.config(['$compileProvider', function($compileProvider) {
  $compileProvider.imgSrcSanitizationWhitelist(/^\s*(https?|data):/);
}]);

var i18n = function(input) {
  if(!input || typeof input === 'string') return input;
  var tag = BennuPortal.locale.tag;
  if(input[tag]) { return input[tag]; }
  tag = tag.split('-')[0];
  if(input[tag]) { return input[tag]; }
  for (var lang in input) {
    if(lang.indexOf(tag) === 0) { return input[lang]; }
  }
  return input[Object.keys(input)[0]];
}

var MD5=function(s){function L(k,d){return(k<<d)|(k>>>(32-d))}function K(G,k){var I,d,F,H,x;F=(G&2147483648);H=(k&2147483648);I=(G&1073741824);d=(k&1073741824);x=(G&1073741823)+(k&1073741823);if(I&d){return(x^2147483648^F^H)}if(I|d){if(x&1073741824){return(x^3221225472^F^H)}else{return(x^1073741824^F^H)}}else{return(x^F^H)}}function r(d,F,k){return(d&F)|((~d)&k)}function q(d,F,k){return(d&k)|(F&(~k))}function p(d,F,k){return(d^F^k)}function n(d,F,k){return(F^(d|(~k)))}function u(G,F,aa,Z,k,H,I){G=K(G,K(K(r(F,aa,Z),k),I));return K(L(G,H),F)}function f(G,F,aa,Z,k,H,I){G=K(G,K(K(q(F,aa,Z),k),I));return K(L(G,H),F)}function D(G,F,aa,Z,k,H,I){G=K(G,K(K(p(F,aa,Z),k),I));return K(L(G,H),F)}function t(G,F,aa,Z,k,H,I){G=K(G,K(K(n(F,aa,Z),k),I));return K(L(G,H),F)}function e(G){var Z;var F=G.length;var x=F+8;var k=(x-(x%64))/64;var I=(k+1)*16;var aa=Array(I-1);var d=0;var H=0;while(H<F){Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=(aa[Z]|(G.charCodeAt(H)<<d));H++}Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=aa[Z]|(128<<d);aa[I-2]=F<<3;aa[I-1]=F>>>29;return aa}function B(x){var k="",F="",G,d;for(d=0;d<=3;d++){G=(x>>>(d*8))&255;F="0"+G.toString(16);k=k+F.substr(F.length-2,2)}return k}function J(k){k=k.replace(/rn/g,"n");var d="";for(var F=0;F<k.length;F++){var x=k.charCodeAt(F);if(x<128){d+=String.fromCharCode(x)}else{if((x>127)&&(x<2048)){d+=String.fromCharCode((x>>6)|192);d+=String.fromCharCode((x&63)|128)}else{d+=String.fromCharCode((x>>12)|224);d+=String.fromCharCode(((x>>6)&63)|128);d+=String.fromCharCode((x&63)|128)}}}return d}var C=Array();var P,h,E,v,g,Y,X,W,V;var S=7,Q=12,N=17,M=22;var A=5,z=9,y=14,w=20;var o=4,m=11,l=16,j=23;var U=6,T=10,R=15,O=21;s=J(s);C=e(s);Y=1732584193;X=4023233417;W=2562383102;V=271733878;for(P=0;P<C.length;P+=16){h=Y;E=X;v=W;g=V;Y=u(Y,X,W,V,C[P+0],S,3614090360);V=u(V,Y,X,W,C[P+1],Q,3905402710);W=u(W,V,Y,X,C[P+2],N,606105819);X=u(X,W,V,Y,C[P+3],M,3250441966);Y=u(Y,X,W,V,C[P+4],S,4118548399);V=u(V,Y,X,W,C[P+5],Q,1200080426);W=u(W,V,Y,X,C[P+6],N,2821735955);X=u(X,W,V,Y,C[P+7],M,4249261313);Y=u(Y,X,W,V,C[P+8],S,1770035416);V=u(V,Y,X,W,C[P+9],Q,2336552879);W=u(W,V,Y,X,C[P+10],N,4294925233);X=u(X,W,V,Y,C[P+11],M,2304563134);Y=u(Y,X,W,V,C[P+12],S,1804603682);V=u(V,Y,X,W,C[P+13],Q,4254626195);W=u(W,V,Y,X,C[P+14],N,2792965006);X=u(X,W,V,Y,C[P+15],M,1236535329);Y=f(Y,X,W,V,C[P+1],A,4129170786);V=f(V,Y,X,W,C[P+6],z,3225465664);W=f(W,V,Y,X,C[P+11],y,643717713);X=f(X,W,V,Y,C[P+0],w,3921069994);Y=f(Y,X,W,V,C[P+5],A,3593408605);V=f(V,Y,X,W,C[P+10],z,38016083);W=f(W,V,Y,X,C[P+15],y,3634488961);X=f(X,W,V,Y,C[P+4],w,3889429448);Y=f(Y,X,W,V,C[P+9],A,568446438);V=f(V,Y,X,W,C[P+14],z,3275163606);W=f(W,V,Y,X,C[P+3],y,4107603335);X=f(X,W,V,Y,C[P+8],w,1163531501);Y=f(Y,X,W,V,C[P+13],A,2850285829);V=f(V,Y,X,W,C[P+2],z,4243563512);W=f(W,V,Y,X,C[P+7],y,1735328473);X=f(X,W,V,Y,C[P+12],w,2368359562);Y=D(Y,X,W,V,C[P+5],o,4294588738);V=D(V,Y,X,W,C[P+8],m,2272392833);W=D(W,V,Y,X,C[P+11],l,1839030562);X=D(X,W,V,Y,C[P+14],j,4259657740);Y=D(Y,X,W,V,C[P+1],o,2763975236);V=D(V,Y,X,W,C[P+4],m,1272893353);W=D(W,V,Y,X,C[P+7],l,4139469664);X=D(X,W,V,Y,C[P+10],j,3200236656);Y=D(Y,X,W,V,C[P+13],o,681279174);V=D(V,Y,X,W,C[P+0],m,3936430074);W=D(W,V,Y,X,C[P+3],l,3572445317);X=D(X,W,V,Y,C[P+6],j,76029189);Y=D(Y,X,W,V,C[P+9],o,3654602809);V=D(V,Y,X,W,C[P+12],m,3873151461);W=D(W,V,Y,X,C[P+15],l,530742520);X=D(X,W,V,Y,C[P+2],j,3299628645);Y=t(Y,X,W,V,C[P+0],U,4096336452);V=t(V,Y,X,W,C[P+7],T,1126891415);W=t(W,V,Y,X,C[P+14],R,2878612391);X=t(X,W,V,Y,C[P+5],O,4237533241);Y=t(Y,X,W,V,C[P+12],U,1700485571);V=t(V,Y,X,W,C[P+3],T,2399980690);W=t(W,V,Y,X,C[P+10],R,4293915773);X=t(X,W,V,Y,C[P+1],O,2240044497);Y=t(Y,X,W,V,C[P+8],U,1873313359);V=t(V,Y,X,W,C[P+15],T,4264355552);W=t(W,V,Y,X,C[P+6],R,2734768916);X=t(X,W,V,Y,C[P+13],O,1309151649);Y=t(Y,X,W,V,C[P+4],U,4149444226);V=t(V,Y,X,W,C[P+11],T,3174756917);W=t(W,V,Y,X,C[P+2],R,718787259);X=t(X,W,V,Y,C[P+9],O,3951481745);Y=K(Y,h);X=K(X,E);W=K(W,v);V=K(V,g)}var i=B(Y)+B(X)+B(W)+B(V);return i.toLowerCase()};

function chunk(arr, n) {
    return arr.slice(0,(arr.length+n-1)/n|0).
           map(function(c,i) { return arr.slice(n*i,n*i+n); });
}

bennuAdmin.filter('i18n', function () {
  return i18n;
});

bennuAdmin.filter('uptime', function () {
  var n = function(n){ return n > 9 ? "" + n: "0" + n; };
  return function(value) { value = value || 0;
    var days = Math.floor(value/(24*60*60)), hours = Math.floor(value/(60*60)) % 24, mins = Math.floor(value/60) % 60;
    return (value >= 86400 ? n(days) + 'd ' : '') + (value >= 3600 ? n(hours) + 'h ' : '') + (value >= 60 ? n(mins) + 'm ' : '') + n(value % 60)+ 's'; };
});

bennuAdmin.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/configuration', {
        templateUrl: contextPath + '/bennu-admin/template/PortalConfiguration.html',
        controller: 'PortalConfigurationCtrl'
      }).
      when('/menu/:id', {
        templateUrl: contextPath + '/bennu-admin/template/Menu.jsp',
        controller: 'MenuController'
      }).
      when('/system/info', {
        templateUrl: contextPath + '/bennu-admin/template/SystemInfo.html',
        controller: 'SystemInfoController'
      }).
      when('/system/logger', {
        templateUrl: contextPath + '/bennu-admin/template/Logger.html',
        controller: 'LoggerController'
      }).
      when('/domain-browser/:oid?', {
        templateUrl: contextPath + '/bennu-admin/template/DomainBrowser.html',
        controller: 'DomainBrowserController'
      }).
      when('/user-management', {
        templateUrl: contextPath + '/bennu-admin/template/UserManagement.html',
        controller: 'UserManagementController'
      }).
      when('/jmx-browser', {
        templateUrl: contextPath + '/bennu-admin/template/JmxBrowser.html',
        controller: 'JmxController'
      }).
      otherwise({
        redirectTo: '/configuration'
      });
  }]);

bennuAdmin.controller('PortalConfigurationCtrl', ['$scope', '$http', function ($scope, $http) {
  $http.get(contextPath + '/api/bennu-portal/configuration').success(function (data) {
    $scope.locales = BennuPortal.locales;
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
  $scope.reload(true);
}]);

bennuAdmin.directive('progressBar', function() {
  return {
    restrict: 'E',
    scope: { 'current': '=', 'total': '=' },
    template: '<p><strong>{{header}}</strong> {{current / divider | number:precision}}{{unit}} / {{total / divider | number:precision}}{{unit}}</p>\
              <div class="progress progress-striped" title="{{ratio() | number}}%">\
                <div class="progress-bar progress-bar-{{barStyle()}}" role="progressbar" style="width: {{ratio() | number:0}}%;"\
                     aria-valuemin="0" aria-valuemax="100" aria-valuenow="{{ratio() | number:0}}">\
                  {{ratio() | number:0}}%\
                </div>\
              </div>',
    link: function(scope, el, attr) {
      scope.ratio = function() { return (scope.current / scope.total) * 100 };
      scope.barStyle = function() {
        var r = Math.round(scope.ratio()); if(r < 75) {return 'success';} if(r < 90) {return 'warning';} return 'danger';
      }
      scope.divider = attr.divider || 1; scope.unit = attr.unit || '';
      scope.precision = attr.precision || 0; scope.header = attr.header || '';
    }
  }
});

bennuAdmin.controller('LoggerController', [ '$scope', '$http', function ($scope, $http) {
  $scope.changeLevel = function(logger, level) {
    $http.get(contextPath + '/api/bennu-core/system/logger/' + logger.name + '/' + level).success(function (data) {
      $scope.loggers = data.loggers;
    });
  }
  $http.get(contextPath + '/api/bennu-core/system/logger').success(function (data) {
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
    $http.get(contextPath + '/api/bennu-core/domain-browser/' + $scope.oid + '/' + name).success(function (data) {
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
  $http.get(contextPath + '/api/bennu-core/domain-browser/' + $scope.oid).success(function (data) {
    document.title = data.oid + " · Domain Browser";
    $scope.data = data;
  }).error(function (data, code) { $scope.error = code; $scope.id = $scope.oid; });
}]);

bennuAdmin.controller('UserManagementController', [ '$scope', '$http', '$routeParams', '$location',
                                                   function ($scope, $http, $routeParams, $location) {
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
            aspectRatio: 1,
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
    $scope.errors = null;
    $http.post(contextPath + '/api/bennu-core/users', user).success(function () {
      $scope.reset();
    }).error(function(data, code) {
      $scope.errors = data.message;
    });
  };

  $scope.reset = function() {
    $scope.avatarchoice = 'upload';
    $scope.user = { preferredLocale: $scope.locales[0] };
  };

  $scope.isUnchanged = function(user) {
    return angular.equals(user, {});
  };

  $scope.locales = BennuPortal.locales;
  $scope.reset();
}]);

bennuAdmin.controller('JmxController', ['$scope', '$http', function($scope, $http) {
  $scope.setDomain = function(name) { $scope.currentDomain = name; }
  $scope.show = function(item) { $scope.selected = item; $('#attributes').modal('show'); }
  $scope.isObject = function(something) { return typeof something === 'object'; }
  $scope.isArray = function(something) { return Array.isArray(something); }
  $http.get(contextPath + '/api/bennu-core/system/jmx').success(function (data) { $scope.data = data; });
}]);

bennuAdmin.filter('capitalize', function() {
  return function(input, scope) {
    input = input.toLowerCase();
    return input.substring(0,1).toUpperCase()+input.substring(1);
  }
});