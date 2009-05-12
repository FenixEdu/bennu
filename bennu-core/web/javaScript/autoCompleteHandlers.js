function clearAutoComplete(input,inputText) {
	id = escapeId(input.attr('id')) + "_AutoComplete";
	$("input#" + id).val("");
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
