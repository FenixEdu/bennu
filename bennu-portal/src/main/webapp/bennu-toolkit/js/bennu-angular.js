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
})();