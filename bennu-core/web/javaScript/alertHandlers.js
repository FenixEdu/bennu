

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
 
function linkConfirmationHookForm(formId, messageKey, titleKey) {
	 var submitButton = $("#" + formId + " :submit");
	    
	    submitButton.attr('onclick','jConfirm('+messageKey+', '+titleKey+',function(userInput) { if (userInput) {$(#'+formId+').submit(); } return false; });return false; ');
	    $("<div class=\"dinline forminline\"><form id='" +  formId + "form' action='" + 'teste' + "' method=\"post\"'></form></div>").insertBefore("#" + formId);    

}
function linkConfirmationHook(linkId, messageKey, titleKey) {
  var href = $("#" + linkId ).attr('href');
  $("#" + linkId).click(function() {
	  requestConfirmation(linkId + "form",messageKey,titleKey);
        });
  $("#" + linkId).attr('href',"#");
  $("<div class=\"dinline forminline\"><form id='" +  linkId + "form' action='" + href + "' method=\"post\"'></form></div>").insertBefore("#" + linkId);
}
