(function () {
	var bennuToolkit = angular.module('bennuToolkit', []);

	bennuToolkit.filter('i18n', function() {
		return function(input) {
			return Bennu.localizedString.getContent(input, Bennu.locale);
		};
	});

	bennuToolkit.directive('ngLocalizedString', ['$timeout', function($timeout) {
	  return {
	    restrict: 'A',
	    scope: {
	      model: '=ngLocalizedString'
	    },
	    link: function(scope, el, attr) {
	      el.hide();
	      var handler = Bennu.localizedString.createWidget(el);
	      scope.$watch('model', function(value) {
	        value = JSON.stringify(value);
	        if(value !== handler.get()) {
	          handler.set(value);
	        }
	      });
	      handler.onchange(function () {
	        $timeout(function () {
	          scope.model = JSON.parse(handler.get());
	        });
	      });
	    }
	  }
	}]);

    bennuToolkit.directive('ngHtmlEditor', ['$timeout', function($timeout) {
        return {
            restrict: 'A',
            scope: {
                model: '=ngHtmlEditor'
            },
            link: function(scope, el, attr) {
                el.hide();
                var isLocalized = el[0].hasAttribute('bennu-localized-string');
                var handler = Bennu.htmlEditor.createWidget(el);
                scope.$watch('model', function(value) {
                    if(isLocalized) {
                        value = JSON.stringify(value);
                    }
                    if(value !== handler.get()) {
                        handler.set(value);
                    }
                });
                handler.onchange(function () {
                    $timeout(function () {
                        scope.model = isLocalized ? JSON.parse(handler.get()) : handler.get();
                    });
                });
            }
        }
    }]);

	bennuToolkit.directive('progressBar', function() {
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

})();