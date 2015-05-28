(function(){
  Bennu.codeEditor = Bennu.codeEditor || {};
  Bennu.codeEditor.require = function(){
    if (!window.ace){
      $.ajaxSetup({cache: true});
      $(document.body).append("<script src='" + Bennu.contextPath + "/bennu-toolkit/js/libs/ace/ace.js'></script>");
    }
  };
})();
