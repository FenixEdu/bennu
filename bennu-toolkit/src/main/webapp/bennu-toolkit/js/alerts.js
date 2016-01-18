(function (argument) {
	window.Bennu = window.Bennu || {};
	Bennu.alerts = Bennu.alerts || {};

	if (!Bennu.contextPath) {
		$("script").map(function (idx, el) {
				var src = el.getAttribute('src');
				if(src && src.indexOf('/bennu-toolkit/js/alerts.js') != -1) {
						Bennu.contextPath = src.substring(0, src.indexOf('/bennu-toolkit/js/alerts.js'));
				}
		});
	}

	var _cbs = [];

	var _alert = function(message,type){
		var cb = function() { _notify(message,type); };
		if ($.notify) {
			// Library loaded, show message
			cb();
		} else {
			var scriptUrl = Bennu.contextPath + "/bennu-toolkit/js/libs/bootstrap-notify.min.js";
			if (Bennu.ensure) {
				Bennu.ensure(scriptUrl, cb);
			} else {
				_cbs.push(cb);
				if (_cbs.length == 1) {
					$.getScript(scriptUrl).done(function () {
						_cbs.map(function (el) { el(); });
						_cbs = [];
					});
				}
			}
		}
	}

	var _notify = function(message,type){
		$.notify({
			// options
			message: message
		},{
			// settings
			type: type,
			offset: {
				y:80,x:20
			},
			animate: {
				enter: 'animated fadeInDown',
				exit: 'animated fadeOutUp'
			},
			template: '<div style="max-width:500px;" class="alert alert-{0} alert-dismissible alert-floating" role="alert"><button type="button" class="close" data-dismiss="alert"><span class="sr-only">Close</span></button><i class="icon icon-check"></i>{2}</div>'
		});
	}

	Bennu.alerts.success = function(message){
		_alert(message,"success");
	}

	Bennu.alerts.info = function(message){
		_alert(message,"info");
	}

	Bennu.alerts.danger = function(message){
		_alert(message,"danger");
	}

	Bennu.alerts.warning = function(message){
		_alert(message,"warning");
	}

	$(function(){
		var a = $("meta[name='alerts']");
		if (a.size()){
			var x = JSON.parse(a.attr("content"));

			for (var i = 0; i < x.length; i++) {
				var alert = x[i];
				_alert(alert.message, alert.type);
			};
		}
	});

})();