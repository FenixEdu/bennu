(function(){

  var t = true;
  Bennu.browser = Bennu.browser || {};
  function detect(ua) {

    function getFirstMatch(regex) {
      var match = ua.match(regex);
      return (match && match.length > 1 && match[1]) || '';
    }

    function getSecondMatch(regex) {
      var match = ua.match(regex);
      return (match && match.length > 1 && match[2]) || '';
    }

    var iosdevice = getFirstMatch(/(ipod|iphone|ipad)/i).toLowerCase();
    var likeAndroid = /like android/i.test(ua);
    var android = !likeAndroid && /android/i.test(ua);
    var edgeVersion = getFirstMatch(/edge\/(\d+(\.\d+)?)/i);
    var versionIdentifier = getFirstMatch(/version\/(\d+(\.\d+)?)/i);
    var tablet = /tablet/i.test(ua);
    var mobile = !tablet && /[^-]mobi/i.test(ua);
    var result

    if (/opera|opr/i.test(ua)) {
      result = {
        name: 'Opera',
        id:'opera',
        opera: true,
        version: versionIdentifier || getFirstMatch(/(?:opera|opr)[\s\/](\d+(\.\d+)?)/i)
      }
    }else if (/yabrowser/i.test(ua)) {
      result = {
        name: 'Yandex Browser',
        yandexbrowser: true,
        id:"yandex",
        version: versionIdentifier || getFirstMatch(/(?:yabrowser)[\s\/](\d+(\.\d+)?)/i)
      }
    }else if (/windows phone/i.test(ua)) {
      result = {
        name: 'Windows Phone',
        id:"windows_phone",
        windowsphone: true
      }
      if (edgeVersion) {
        result.msedge = t
        result.version = edgeVersion
      }
      else {
        result.msie = true
        result.version = getFirstMatch(/iemobile\/(\d+(\.\d+)?)/i)
      }
    }else if (/msie|trident/i.test(ua)) {
      result = {
        name: 'Internet Explorer', 
        msie: true, 
        id: "msie",
        version: getFirstMatch(/(?:msie |rv:)(\d+(\.\d+)?)/i),
      }
    }else if (/chrome.+? edge/i.test(ua)) {
      result = {
        name: 'Microsoft Edge', 
        id:"msedge",
        msedge: true, 
        version: edgeVersion
      }
    }else if (/chrome|crios|crmo/i.test(ua)) {
      result = {
        name: 'Chrome', 
        id: "chrome",
        chrome: true, 
        version: getFirstMatch(/(?:chrome|crios|crmo)\/(\d+(\.\d+)?)/i)
      }
    }else if (/kindle/i.test(ua)) {
      result = {
        name: 'Kindle', 
        id: "kindle",
        chrome: true, 
        version: getFirstMatch(/(?:kindle)\/(\d+(\.\d+)?)/i)
      }
    }else if (iosdevice) {
      result = {
        name : iosdevice == 'iphone' ? 'iPhone' : iosdevice == 'ipad' ? 'iPad' : 'iPod'
      }
      result.id = result.name.toLowerCase();

      // WTF: version is not part of user agent in web apps
      if (versionIdentifier) {
        result.version = versionIdentifier
      }
    }else if (/sailfish/i.test(ua)) {
      result = {
        name: 'Sailfish',
        id:"sailfish",
        sailfish: true,
        version: getFirstMatch(/sailfish\s?browser\/(\d+(\.\d+)?)/i)
      }
    }else if (/seamonkey\//i.test(ua)) {
      result = {
        name: 'SeaMonkey', 
        id:"seamonkey",
        seamonkey: true, 
        version: getFirstMatch(/seamonkey\/(\d+(\.\d+)?)/i)
      }
    }else if (/firefox|iceweasel/i.test(ua)) {
      result = {
        name: 'Firefox', 
        firefox: t, 
        id: "firefox",
        version: getFirstMatch(/(?:firefox|iceweasel)[ \/](\d+(\.\d+)?)/i)
      }
      if (/\((mobile|tablet);[^\)]*rv:[\d\.]+\)/i.test(ua)) {
        result.firefoxos = true
      }
    }else if (/silk/i.test(ua)) {
      result =  {
        name: 'Amazon Silk', 
        id:'silk',
        silk: true, 
        version : getFirstMatch(/silk\/(\d+(\.\d+)?)/i)
      }
    }else if (android) {
      result = {
        name: 'Android',
        id: 'android',
        version: versionIdentifier
      }
    }else if (/phantom/i.test(ua)) {
      result = {
        name: 'PhantomJS', 
        id: "phantomjs",
        phantom: true,
        version: getFirstMatch(/phantomjs\/(\d+(\.\d+)?)/i)
      }
    }else if (/blackberry|\bbb\d+/i.test(ua) || /rim\stablet/i.test(ua)) {
      result = {
        name: 'BlackBerry', 
        id: "blackberry",
        blackberry: true,
        version: versionIdentifier || getFirstMatch(/blackberry[\d]+\/(\d+(\.\d+)?)/i)
      }
    }else if (/(web|hpw)os/i.test(ua)) {
      result = {
        name: 'WebOS', 
        webos: true, 
        id: "webos",
        version: versionIdentifier || getFirstMatch(/w(?:eb)?osbrowser\/(\d+(\.\d+)?)/i)
      };
      /touchpad\//i.test(ua) && (result.touchpad = t)
    }else if (/bada/i.test(ua)) {
      result = {
        name: 'Bada', 
        id: 'bada', 
        bada: true, 
        version: getFirstMatch(/dolfin\/(\d+(\.\d+)?)/i)
      };
    }else if (/tizen/i.test(ua)) {
      result = {
        name: 'Tizen', 
        tizen: true, 
        id: 'tizen',
        version: getFirstMatch(/(?:tizen\s?)?browser\/(\d+(\.\d+)?)/i) || versionIdentifier
      };
    }
    else if (/safari/i.test(ua)) {
      result = {
        name: 'Safari', 
        safari: true, 
        id: 'safari',
        version: versionIdentifier
      }
    }else {
      result = {
        name: getFirstMatch(/^(.*)\/(.*) /),
        id: getFirstMatch(/^(.*)\/(.*) /).toLowerCase(),
        version: getSecondMatch(/^(.*)\/(.*) /)
     };
   }

    // set webkit or gecko flag for browsers based on these engines
    if (!result.msedge && /(apple)?webkit/i.test(ua)) {
      result.name = result.name || "Webkit"
      result.id = result.id || "webkit"
      result.webkit = true;
      if (!result.version && versionIdentifier) {
        result.version = versionIdentifier
      }
    } else if (!result.opera && /gecko\//i.test(ua)) {
      result.name = result.name || "Gecko"
      result.id = result.id || "gecko"
      result.gecko = true
      result.version = result.version || getFirstMatch(/gecko\/(\d+(\.\d+)?)/i)
    }

    // set OS flags for platforms that have multiple browsers
    if (!result.msedge && (android || result.silk)) {
      result.android = true;
    } else if (iosdevice) {
      result[iosdevice] = t
      result.ios = true;
    }

    // OS version extraction
    var osVersion = '';
    if (result.windowsphone) {
      osVersion = getFirstMatch(/windows phone (?:os)?\s?(\d+(\.\d+)*)/i);
    } else if (iosdevice) {
      osVersion = getFirstMatch(/os (\d+([_\s]\d+)*) like mac os x/i);
      osVersion = osVersion.replace(/[_\s]/g, '.');
    } else if (android) {
      osVersion = getFirstMatch(/android[ \/-](\d+(\.\d+)*)/i);
    } else if (result.webos) {
      osVersion = getFirstMatch(/(?:web|hpw)os\/(\d+(\.\d+)*)/i);
    } else if (result.blackberry) {
      osVersion = getFirstMatch(/rim\stablet\sos\s(\d+(\.\d+)*)/i);
    } else if (result.bada) {
      osVersion = getFirstMatch(/bada\/(\d+(\.\d+)*)/i);
    } else if (result.tizen) {
      osVersion = getFirstMatch(/tizen[\/\s](\d+(\.\d+)*)/i);
    }
    if (osVersion) {
      result.osversion = osVersion;
    }

    // device type extraction
    var osMajorVersion = osVersion.split('.')[0];
    if (tablet || iosdevice == 'ipad' || (android && (osMajorVersion == 3 || (osMajorVersion == 4 && !mobile))) || result.silk) {
      result.tablet = true;
    } else if (mobile || iosdevice == 'iphone' || iosdevice == 'ipod' || android || result.blackberry || result.webos || result.bada) {
      result.mobile = true;
    }

    // Graded Browser Support
    // http://developer.yahoo.com/yui/articles/gbs
    if (result.msedge ||
        (result.msie && result.version >= 10) ||
        (result.yandexbrowser && result.version >= 15) ||
        (result.chrome && result.version >= 20) ||
        (result.firefox && result.version >= 20.0) ||
        (result.safari && result.version >= 6) ||
        (result.opera && result.version >= 10.0) ||
        (result.ios && result.osversion && result.osversion.split(".")[0] >= 6) ||
        (result.blackberry && result.version >= 10.1)
        ) {
      result.a = t;
    }
    else if ((result.msie && result.version < 10) ||
        (result.chrome && result.version < 20) ||
        (result.firefox && result.version < 20.0) ||
        (result.safari && result.version < 6) ||
        (result.opera && result.version < 10.0) ||
        (result.ios && result.osversion && result.osversion.split(".")[0] < 6)
        ) {
      result.c = true;
    } else result.x = true;

    return result
  }

  Bennu.browser = Bennu.browser || {};

  Bennu.browser.ua = function(x){
    if(x === undefined){
      return Bennu.browser._ua;
    }else{
      Bennu.browser._ua = x;
      Bennu.browser._meta = detect(x);
      return x;
    }
  };

  Bennu.browser.ua(typeof navigator !== 'undefined' ? navigator.userAgent : '');

  Bennu.browser.locale = function() {
      return (navigator.userLanguage || navigator.language || 'en-US').toLowerCase();
  };

  Bennu.browser.name = function() {
      return this._meta.name;
  };

  Bennu.browser.id = function() {
      return this._meta.id;
  };

  Bennu.browser.version = function() {
      if(this._meta.version){
        var version = this._meta.version + "" || '0.0';
        
        if (version.indexOf(".") == -1){
          version = version + ".0";
        }
        return new Bennu.semanticVersion(version);
      }else{
        return new Bennu.semanticVersion('0.0');
      }
  };


  Bennu.browser.is_capable = function() {
      return this.uses_webkit() || this.is_firefox() || this.is_opera() || (this.is_ie() && this.version().major >= 7);
  };

  Bennu.browser.is_supported = function() {
      return this.uses_webkit() || this.is_firefox() || this.is_opera() || (this.is_ie() && this.version().major >= 9);
  };

  Bennu.browser.uses_webkit = function() {
      return this._meta.webkit || false;
  };

  Bennu.browser.on_ios = function() {
      return this.on_ipod() || this.on_ipad() || this.on_iphone();
  };

  Bennu.browser.on_mobile = function() {
      return this._meta.mobile;
  };

  Bennu.browser.on_blackberry = function() {
      return this._meta.id == "blackberry";
  };

  Bennu.browser.on_android = function() {
      return this._meta.id == "android";
  };

  Bennu.browser.on_iphone = function() {
      return this._meta.id == "iphone";
  };

  Bennu.browser.on_ipad = function() {
      return this._meta.id == "ipad";
  }

  Bennu.browser.on_ipod = function() {
      return this._meta.id == "ipod";
  };

  Bennu.browser.is_safari = function() {
      return this._meta.id == "safari";
  };


  Bennu.browser.is_firefox = function() {
      return this._meta.id == "firefox";
  }
   
  Bennu.browser.is_chrome = function() {
      return this._meta.id == "chrome"
  };

  Bennu.browser.is_ie = function() {
      return this._meta.id == "msie"
  };

  Bennu.browser.is_ie6 = function() {
      return this.is_ie() && this.version().major === '6';
  };

  Bennu.browser.is_ie7 = function() {
      return this.is_ie() && this.version().major === '7';
  };
  Bennu.browser.is_ie8 = function() {
      return this.is_ie() && this.version().major === '8';
  };

  Bennu.browser.is_ie9 = function() {
      return this.is_ie() && this.version().major === '9';
  };

  Bennu.browser.is_ie10 = function() {
      return this.is_ie() && this.version().major === '10';
  };

  Bennu.browser.is_ie11 = function() {
      return this.is_ie() && this.version().major === '11';
  };
  
  Bennu.browser.is_opera = function() {
      return this._meta.id == "opera"
  };

  Bennu.browser.on_mac = function() {
      return !!this._ua.match(/Mac OS X/);
  };
  
  Bennu.browser.on_windows = function() {
      return !!this._ua.match(/Windows/);
  };
  
  Bennu.browser.on_linux = function() {
      return !!this._ua.match(/Linux/);
  };

  Bennu.browser.on_tablet = function() {
      return this._meta.tablet || false;
  };

  Bennu.browser.on_kindle = function() {
      return !!this._ua.match(/Kindle/);
  };

  Bennu.browser.platform = function() {
      if (this.on_linux()) {
        return 'linux';
      } else if (this.on_mac()) {
        return 'mac';
      } else if (this.on_windows()) {
        return 'windows';
      } else {
        return 'other';
      }
  };

  Bennu.browser.osVersion = function() {
    if (this._meta.osVersion){
      if (this._meta.osVersion.indexOf(".") == -1){
        return this._meta.osVersion(this._meta.osVersion + ".0");
      }else{
        return this._meta.osVersion(this._meta.osVersion);
      }
    }else{
      return new Bennu.semanticVersion("0.0");
    }
  };

  Bennu.browser.meta = function() {
      return this._meta;
    };

  Bennu.browser.a = function(cb) {
    if (this._meta.a){
      cb && cb();
    }
    return this;
  };

  Bennu.browser.c = function(cb) {
    if (this._meta.c){
      cb && cb();
    }
    return this;
  };

  Bennu.browser.x = function(cb) {
    if (this._meta.c){
      cb && cb();
    }
    return this;
  };

  Bennu.browser.toString = function() {
      return this.name() + " " + this.version().toString();
  }
})()