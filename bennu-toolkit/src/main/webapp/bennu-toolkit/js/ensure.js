(function(){
	var loaded = {};
	var cbs = {};

	var DependentCallback = function(deps, cb){
		this.isFulfilled = function(){
			var val = $.map(deps, function(v){
				return Bennu.ensure.isLoaded(v);
			});

			return Array.prototype.reduce.apply(val, [function (x, y) {
                        return x && y;
                    }, true]);
		}

		this.call = function(){
			if(this.isFulfilled()){
				cb();
			}
		}
	};

	Bennu.ensure = Bennu.ensure || function(files,cb){
		if (typeof files == "string"){
			files = [files];
		}
		var d = new DependentCallback(files, cb);
	    if (!d.isFulfilled()){
	    	for (var i = 0; i < files.length; i++) {
	    		var file = files[i]

	    		if (!Bennu.ensure.isLoaded(file)){
		      		if (!cbs[file]){
		      			cbs[file] = [d];
		      			(function(file){
			      			var script = document.createElement('script');

							script.onload = function() {
								loaded[file] = true;
								for (var i = 0; i < cbs[file].length; i++) {
									cbs[file][i].call();
								};
							};

							script.src = file + "?ver=" + Bennu.version.version;
							document.getElementsByTagName('head')[0].appendChild(script);
		      			})(file)
			      	}else{
						cbs[file].push(d);
					}
				}
			};
	    }else{
	    	cb();
	    }
	};

	Bennu.ensure.isLoaded = function(files){
		if (typeof files == "string"){
			return loaded[files] || false;
		}else{
			return Array.prototype.reduce.apply($.map(files, function(x){
				return loaded[files] || false;;
			}), [function (x, y) {
                return x && y;
            }, true]);
		}

	};
})();
