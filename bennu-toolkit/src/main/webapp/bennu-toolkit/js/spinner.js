(function(){
    Bennu.spinner = Bennu.spinner || {};

    Bennu.spinner.attr = "bennu-spinner";

    Bennu.spinner.createWidget = function(e){
      e = $(e);
      var template = "<i class='bennu-spinner-widget icon icon-spin'></i>";
      var dom = $(template);
      

      e.data("widget",dom);
      dom.data("related",e);

      if (Bennu.utils.hasAttr(e,"faster")){
        Bennu.spinner.faster(e);
      }

      if (Bennu.utils.hasAttr(e,"slower")){
        Bennu.spinner.slower(e);
      }

      if (Bennu.utils.hasAttr(e,"color")){
        dom.css("color",Bennu.theme[$(e).attr("color")]());
      }

      e.after(dom);
    };

    Bennu.spinner.faster = function(e){
      e=$(e);
      e.data("widget").addClass("bennu-spinner-faster");
      e.data("widget").removeClass("bennu-spinner-slower");
    }

    Bennu.spinner.normal = function(e){
      e=$(e);
      e.data("widget").removeClass("bennu-spinner-faster");
      e.data("widget").removeClass("bennu-spinner-slower");
    }

    Bennu.spinner.slower = function(e){
      e=$(e);
      e.data("widget").removeClass("bennu-spinner-faster");
      e.data("widget").addClass("bennu-spinner-slower");
    }

})();