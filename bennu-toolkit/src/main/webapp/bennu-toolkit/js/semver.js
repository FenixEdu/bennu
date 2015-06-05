/*
 * semver.js
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

(function(){
	Bennu.semanticVersion = Bennu.semanticVersion || function(version){
		var match = /^[^\d]*(\d+)\.(\d+)(\.(\d+))?(.*)?/g.exec(version);

		if (!match){
			return null;
		}

		this.major    = parseInt(match[1]);
		this.minor    = parseInt(match[2]);
		this.revision = parseInt(match[4] || 0);

		var rest = match[4];
		if (rest){
			if (rest == "-SNAPSHOT"){
				this.snapshot = true;
			}

			match = rest.toUpperCase().match(/^[\-\.]?A(LPHA)?\-?(\d+)/);

			if(match){
				this.alpha = parseInt(match[2]);
			}

			match = rest.toUpperCase().match(/^[\-\.]?B(ETA)?[\-\.]?(\d+)/);

			if(match){
				this.beta = parseInt(match[2]);
			}

			match = rest.toUpperCase().match(/^[\-\.]?RC(\d+)/);

			if(match){
				this.releaseCandidate = parseInt(match[1]);
			}

			match = rest.toUpperCase().match(/^[\-\.]?M(\d+)/);
			if(match){
				this.milestone = parseInt(match[1]);
			}
		}

		this.version = version;
	};

	Bennu.semanticVersion.prototype.toString = function(){
		return this.version;
	};

	Bennu.semanticVersion.prototype.cmp = function(that){
		if (this.major !== that.major){
			return this.major - that.major
		}

		if (this.minor !== that.minor){
			return this.minor - that.minor
		}

		if (this.revision !== that.revision){
			return this.revision - that.revision
		}
		var props = ["alpha","beta","milestone","releaseCandidate","snapshot"]
		var k = 0;

		var vThis = this;
		var vThat = that;
		var result;
		$.map(props, function(p){
			if(vThis[p]){
				var l = k;
				$.map(props, function(s){
					if(vThat[s]){
						if(s == p){
							if (s === "snapshot"){
								result = result || 0
							}else{
								result = result || vThis[p] -vThat[s];
							}
						}else{
							result = result || l;
						}
					}
					l -= 1;
				});
				result = result || 	l;
			}
			k += 1;
		});
		return result || 0;
	};

	Bennu.semanticVersion.prototype.before = function(version, cb){
		if (typeof version === "string"){
			version = new Bennu.semanticVersion(version);
		}

		if(this.cmp(version) <= 0){
			try{
				cb(version);
			}catch(e){
				console.error(e);
			}
		}

		return this;
	}

	Bennu.semanticVersion.prototype.after = function(version, cb){
		if (typeof version === "string"){
			version = new Bennu.semanticVersion(version);
		}

		if(this.cmp(version) >= 0){
			try{
				cb(version);
			}catch(e){
				console.error(e);
			}
		}
		
		return this;
	}

	Bennu.semanticVersion.prototype.between = function(v1,v2, cb){
		if (typeof v1 === "string"){
			v1 = new Bennu.semanticVersion(v1);
		}

		if (typeof v2 === "string"){
			v2 = new Bennu.semanticVersion(v2);
		}

		if(this.cmp(v1) >= 0 && this.cmp(v2) <= 0){
			try{
				cb(v1,v2);
			}catch(e){
				console.error(e);
			}
		}
		
		return this;
	}

	Bennu.version = new Bennu.semanticVersion(Bennu.version) || new Bennu.semanticVersion("1.0.0-SNAPSHOT");
})();