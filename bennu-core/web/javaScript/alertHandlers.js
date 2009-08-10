

 function requestConfirmation(formId,messageKey,titleKey) {
	 $.alerts.overlayOpacity= 0.4;
	 $.alerts.overlayColor= '#333';
	 jConfirm(messageKey, titleKey,function(userInput) {
		  if(userInput) {
			  $("#" + formId).submit(); 
           }
        });
}
 
 function requestConfirmationForJQueryForm(form,messageKey,titleKey) {
	 $.alerts.overlayOpacity= 0.4;
	 $.alerts.overlayColor= '#333';
	 jConfirm(messageKey, titleKey,function(userInput) {
		  if(userInput) {
			 form.submit(); 
           }
        });
} 
 

function linkConfirmationHook(linkId, messageKey, titleKey) {
  var href = $("#" + linkId ).attr('href');
  $("#" + linkId).click(function() {
	  requestConfirmation(linkId + "form",messageKey,titleKey);
        });
  $("#" + linkId).attr('href',"#");
  $("<form id='" +  linkId + "form' action='" + href + "' method=\"post\"'></form>").insertAfter("#" + linkId);
}
