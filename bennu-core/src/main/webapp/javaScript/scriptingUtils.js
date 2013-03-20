
/*
 * 
 * Various utility java script functions
 * Paulo Abrantes
 * 
 **/
	

 /*
  * formatString formats a string with the {i} format using the formatTokens[i] 
  * 
  */
function formatString(string, formatTokens) {
	var text = string;
	for (i=0; i < formatTokens.length ; i++) {
		text = text.replace(new RegExp("\\{" + i + "\\}"),formatTokens[i]);
	}
	return text;
}

/*
 * ajaxRequestOnClick binds an ajax GET request to 'link' when
 * elements of clickBindSelector are clicked and replaces replaceSelector
 * with the received data. So data should be HTML!
 * 
 * Optionally, can be specified hideSelector and showSelector which will allow
 * elements to be hidden and another ones to be shown.
 */

function ajaxRequestOnClick(clickBindSelector, link, replaceSelector, hideSelector, showSelector) {
	$(clickBindSelector).click(function() {
		$.get(link, function(data,textStatus) {
			$(replaceSelector).replaceWith(data);
		});
		if (hideSelector != null) {
			$(hideSelector).hide();
		}
		if (showSelector != null) {
			$(showSelector).show();
		}
		
	});	
}

