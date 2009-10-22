function clearAutoComplete(input,inputText) {
	id = escapeId(input.attr('id')) + "_AutoComplete";
	$("input#" + id).val("");
	$(input).parents("td").next("td:last").fadeOut();
}
function updateCustomValue(input,inputText) {
	id = escapeId(input.attr('id')) + "_OldValue";
	$("input#" + id).val( inputText);
}
function selectElement(input,inputText,element) {
	id = escapeId(input.attr('id'));  
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