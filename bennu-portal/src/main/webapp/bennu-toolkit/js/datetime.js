(function(){
  $(function(){
    $("[bennu-datetime]").each(function(i,e){
      e = $(e);
      var dom = $('<div class="bennu-datetime-input input-group date"><span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span><input type="text" class="form-control"/></div>');
      $("input", dom).on("input propertychange", function (x) {
          x = $(x.target);
          e.val(x.val());
      }).datetimepicker({ useSeconds:true });

      e.after(dom);
    });
  });
}());
