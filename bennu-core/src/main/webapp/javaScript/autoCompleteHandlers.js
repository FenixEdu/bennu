function clearAutoComplete(input,inputText) {
	var id = escapeId(input.attr('id')) + "_AutoComplete";
	$("input#" + id).val("");
	$(input).parents("td").next("td:last").fadeOut();
}
function updateCustomValue(input,inputText) {
	var id = escapeId(input.attr('id')) + "_OldValue";
	$("input#" + id).val( inputText);
	$("input#" + escapeId(input.attr('id')) + "_AutoComplete").val("custom");
	var name = escapeId(input.attr('id'));
	$("input[name='" +  name + "']").val($("input#" + id).val());
}
function selectElement(input,inputText,element) {
	var id = escapeId(input.attr('id'));  
	$("input#" + id + "_AutoComplete").val(element.attr('id')); 
	$("input#" + id + "_OldValue").val( inputText);
	$("input#" + id + "_AutoComplete").trigger("change");
}        
function escapeId(id) {          
	return id.replace(/\./g,"\\.").replace(/\:/g,"\\:");  
}
function showError(input,inputText) {
	$(input).parents("td").next("td:last").fadeIn(300).html('<span>Ocorreu um erro no servidor, por favor volte a tentar.</span>'); 
}