/*
 * bennu-angular.js
 * 
 * Copyright (c) 2014, Instituto Superior Técnico. All rights reserved.
 * 
 * This file is part of Bennu Toolkit.
 * 
 * Bennu Toolkit is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * Bennu Toolkit is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Bennu Toolkit. If not, see
 * <http://www.gnu.org/licenses/>.
 */

(function () {
	var bennuToolkit = angular.module('bennuToolkit', []);

	bennuToolkit.filter('i18n', function() {
		return function(input) {
			return Bennu.localizedString.getContent(input, Bennu.locale);
		};
	});

	bennuToolkit.directive('bennuLocalizedString', ['$timeout', function($timeout) {
	  return {
	    restrict: 'A',
	    scope: {
	      model: '=bennuLocalizedString'
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

	function toolkitDirective(name, widgetProvider) {
		bennuToolkit.directive(name, ['$timeout', function($timeout) {
		  return {
		    restrict: 'A',
		    scope: {
		      model: '=' + name
		    },
		    link: function(scope, el, attr) {
		      el.hide();
		      var handler = widgetProvider(el);
		      scope.$watch('model', function(value) {
		        if(value !== handler.get()) {
		          handler.set(value);
		        }
		      });
		      handler.onchange(function () {
		        $timeout(function () {
		          scope.model = handler.get();
		        });
		      });
		    }
		  }
		}]);
	}

	toolkitDirective('bennuDateTime', Bennu.datetime.createDateTimeWidget);
	toolkitDirective('bennuDate', Bennu.datetime.createDateWidget);
	toolkitDirective('bennuTime', Bennu.datetime.createTimeWidget);
	toolkitDirective('bennuUserAutocomplete', Bennu.userAutocomplete.createWidget);

	htmlEditor('bennuHtmlEditor', false);
	htmlEditor('bennuLocalizedHtmlEditor', true);

	function htmlEditor(name, isLocalized) {
	    bennuToolkit.directive(name, ['$timeout', function($timeout) {
	        return {
	            restrict: 'A',
	            scope: {
					model: '=' + name,
					onImageAdded: '=onImageAdded'
	            },
	            link: function(scope, el, attr) {
	                el.hide();

	                if (isLocalized) {
	                	// The htmlEditor's createWidget should receive this as a flag,
	                	// not require the attribute to be present.
	                	el.attr('bennu-localized-string', 'true');
	                }
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


					el.data("fileHandler", function (files, callback) {
						if (scope.onImageAdded) {
							scope.onImageAdded(files, callback, handler)
						}
					});
				}
	        }
	    }]);
	};

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

	bennuToolkit.config(['$httpProvider',function($httpProvider) {
		$httpProvider.defaults.headers.common = $httpProvider.defaults.headers.common || {};
		$httpProvider.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';
	}]);

})();