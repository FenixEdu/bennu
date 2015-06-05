
var tableMgmt = function(table,at){

    return {
      '_table':$(table)[0],
      '_atRow':at.row,
      '_atColumn':at.column,
      findMaxColumns:function(){
        var lengths = $.map(this._table.rows,function(i,e){
          return i.children.length;
        });
        return Math.max.apply(Math,lengths);
      },
      'addCellBefore':function(){
        var row = this._table.rows[this._atRow];
        var newCell  = row.insertCell(this._atColumn);
        var newText  = document.createTextNode('\u00A0');
        newCell.appendChild(newText);
      },
      'addCellAfter':function(){
        var row = this._table.rows[this._atRow];
        var newCell  = row.insertCell(this._atColumn + 1);
        var newText  = document.createTextNode('\u00A0');
        newCell.appendChild(newText);
      },
      'mergeCell':function(){

      },
      'horizontalSplit':function(){

      },
      'verticalSplit':function(){
          
      },
      'addRowBefore':function(){
          var row = this._table.insertRow(this._atRow);

          var max = this.findMaxColumns();

          for (var i=0; i<max; i++){
            var newCell  = row.insertCell(0);
            var newText  = document.createTextNode('\u00A0');
            newCell.appendChild(newText);
          }
      },
      'addRowAfter':function(){
          var row = this._table.insertRow(this._atRow + 1);

          var max = this.findMaxColumns();

          for (var i=0; i<max; i++){
            var newCell  = row.insertCell(0);
            var newText  = document.createTextNode('\u00A0');
            newCell.appendChild(newText);
          }
      },
      'addColumnBefore':function(){
          for (var i=0; i<this._table.rows.length; i++){
            var row  = this._table.rows[i];
            var newCell = row.insertCell(this._atColumn);
            var newText  = document.createTextNode('\u00A0');
            newCell.appendChild(newText);
          }       
      },
      'addColumnAfter':function(){
          for (var i=0; i<this._table.rows.length; i++){
            var row  = this._table.rows[i];
            var newCell = row.insertCell(this._atColumn + 1);
            var newText  = document.createTextNode('\u00A0');
            newCell.appendChild(newText);
          }
      },
      'deleteCell':function(){
        this._table.rows[this._atRow].deleteCell(this._atColumn);
      },
      'deleteRow':function(){
        this._table.deleteRow(this._atRow);
      },
      'deleteColumn':function(){
        for (var i=0; i<this._table.rows.length; i++){
            var row  = this._table.rows[i];
            if (this._atColumn < row.children.length){
              row.deleteCell( this._atColumn );
            }
          }
      }
    }
};



function editor(element, handler){
    Bennu.htmlEditor.components.setTitle("Table");
    Bennu.htmlEditor.components.setSubtitle((element?"Edit":"Add") + " a table");

    var output = '<div class="form-group">'+
            '<label for="inputEmail3" class="col-sm-2 control-label">Width</label>'+
              '<div class="col-sm-10">'+
                  '<input type="email" class="form-control" id="width" placeholder="Width" \\>'+
                  '<p class="help-block">To use the full width keep this field empty</p>'+
              '</div>'+
            '</div>'+
          '<div class="checkbox">'+
              '<label>'+
                '<input type="checkbox" id="header" \\> Has a header'+
                '<p class="help-block">Using an header adds an extra row</p>'+
              '</label>'+ 
          '</div>'+

          '<div class="checkbox">'+
              '<label>'+
                '<input type="checkbox" id="zebra" \\> Zebra pattern'+
                '<p class="help-block">On very large tables, this make them easier to read</p>'+
              '</label>'+
          '</div>'+

          '<div class="checkbox">'+
              '<label>'+
                '<input type="checkbox" id="hover" \\> Selects line'+
              '</label>'+
          '</div>';
      

      if (!element){
        output = '<div class="form-group">'+
          '<label for="inputEmail3" class="col-sm-2 control-label">Columns</label>'+
            '<div class="col-sm-4">'+
                '<input type="email" class="form-control" id="columns" placeholder="Columns" \\>'+
                '<p class="help-block"></p>'+
            '</div>'+
          '<label for="inputEmail3" class="col-sm-2 control-label">Rows</label>'+
            '<div class="col-sm-4">'+
                '<input type="email" class="form-control" id="rows" placeholder="Rows" \\>'+
                '<p class="help-block"></p>'+
            '</div>' +
          '</div>' + output;
      }

    output = $('<div class="form-horizontal">' + output + '</div>');
    
    var columns = $("#columns", output);
    var rows = $("#rows", output);
    var width = $("#width", output);
    var bordered = $("#bordered", output);
    var header = $("#header", output);
    var zebra = $("#zebra", output);
    var hover = $("#hover", output);

    if (element){
        width.val(element.attr("data-width"));
        var metadata = JSON.parse(element.attr("data-metadata"));
        bordered.prop('checked',metadata.bordered && true || false);
        header.prop('checked', $("thead",element).children().length > 0);
        zebra.prop('checked',metadata.zebra && true || false);
        hover.prop('checked',metadata.hover && true || false);
    }

    Bennu.htmlEditor.components.setBody(output);
    Bennu.htmlEditor.components.showPrimaryButton();
    Bennu.htmlEditor.components.setPrimaryButton(element?"Edit":"Add");
    Bennu.htmlEditor.components.clickPrimaryButton(function(){
        handler.restore();
        var prevent = false;
        $("#width .help-block", output).html("");

        if (width.val().trim() != ""){
            var w = parseInt(width.val())
            if (!w){
                Bennu.validation.addError($("#width",output).closest(".form-group"));
            }
        }

        w = w || null;

        if (!element){
            element = $('<table bennu-component="table"><thead></thead><tbody></tbody>'+'</table>');
            var tobdy = $("tbody", element)

            for (var i = 0; i < parseInt(rows.val()); i++) {
              var row = $("<tr></tr>")
              for (var j = 0; j < parseInt(columns.val()); j++) {
                row.append("<td>&nbsp;</td>");
              };
              tobdy.append(row)
            };

            if(header.prop("checked")){
              var thead = $("thead", element)
              var row = $("<tr></tr>")
              for (var j = 0; j < parseInt(columns.val()); j++) {
                row.append("<th>&nbsp;</th>");
              };
              thead.append(row)
            }
        }else{
          var thead = $("thead", element);
          var tr = $($("tbody tr", element)[0]);
          var size = tr.children().length

          if (header.prop("checked") == true && thead.children().length == 0){
              var row = $("<tr></tr>")
              for (var j = 0; j < size; j++) {
                row.append("<th>&nbsp;</th>");
              };
              thead.append(row)
          }else if(header.prop("checked") == false && thead.children().length > 0){
            thead.empty();
          }
        }
        
        if (w){
          element.attr("data-width", w);  
        }else{
          element.attr("data-width",null);
          element.removeAttr("data-width");
        }
        
        element.attr("data-metadata", JSON.stringify({
            zebra : zebra.prop('checked') && true || false,
            hover : hover.prop('checked') && true || false,
        }));
        handler.text(element); 
      
	      Bennu.htmlEditor.components.hideModal();
    });
}

Bennu.htmlEditor.components.callback.table = editor;
Bennu.htmlEditor.components.preview.table = function(e){
      e.addClass("table");
      e.addClass("table-bordered");

      if (e.attr("data-width") && parseInt(e.attr("data-width"))){
        e.css("width",e.attr("data-width") + "px");
      }

      if (e.attr("data-metadata")){
        var metadata = JSON.parse(e.attr("data-metadata"))

        if(metadata.zebra){
          e.addClass("table-striped");
        }

        if(metadata.hover){
          e.addClass("table-hover");
        }
      }
};

Bennu.htmlEditor.components.menu.table = function(element, handler){
    var z = function(text,fn){
        text = $(text);
        handler.add(text);
        text.on("click",function(e){
            fn();
        });
    }
    
    var target = handler.target();
    var col = -1;
    var row = -1;
    
    while(true){
      if (target.prop("tagName") == "TD"){
        col = target[0].cellIndex;
        row = target.parent()[0].rowIndex;
        break;
      }if (target[0] == element || !target.length || !target){
        break;
      }else{
        target = target.parent()
      }
    }

    var mgmt = new tableMgmt(element,{row:row, column:col});

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert cell before</a></li>", function(){
      mgmt.addCellBefore();
    });
    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert cell after</a></li>", function(){
      mgmt.addCellAfter();
    });

    // z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Merge cell</a></li>", function(){

    // });

    // z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Horizontal cell split</a></li>", function(){

    // });

    // z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Vertical cell split</a></li>", function(){

    // });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert row before</a></li>", function(){
      mgmt.addRowBefore();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert row after</a></li>", function(){
      mgmt.addRowAfter();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert column before</a></li>", function(){
      mgmt.addColumnBefore();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Insert column after</a></li>", function(){
      mgmt.addColumnAfter();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Delete cell</a></li>", function(){
      mgmt.deleteCell();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Delete row</a></li>", function(){
      mgmt.deleteRow();
    });

    z("<li><a class='pointer' href='#'><i class='glyphicon glyphicon-pencil'></i> Delete column</a></li>",function(){
      mgmt.deleteColumn();
    });

}
