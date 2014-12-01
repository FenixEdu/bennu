(function(){
  Bennu.codeEditor = Bennu.codeEditor || {};
  Bennu.codeEditor.require = function(){
    if (!window.ace){
      $(document.body).append("<script src='" + Bennu.contextPath + "/bennu-toolkit/js/ace/ace.js'></script>");
    }
  };
})();