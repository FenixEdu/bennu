/**
 * Simple validation script based on the idea from
 * GeekTantra at http://www.geektantra.com/projects/jquery-form-validate/
 * 
 * Paulo Abrantes
 * 23-09-2009
 */

(function(jQuery){
    jQuery.fn.validate = function(options){
        options = jQuery.extend({
        	bindEventsTo: this,
        	errorMessage: 'error',
            validationHandler: null,
            clearHandler: defaultClearHandler,
            errorHandler: defaultErrorHandler,
            successHandler: null
        }, options);
    
        jQuery(options['bindEventsTo']).bind('blur', function(){
        	setTimeout(validateField, 100, $(this));
        });
       
        jQuery(options['bindEventsTo']).bind('focus keypress', function(){
           if (options['clearHandler'] != null) {
        	   options['clearHandler'](this);
           }
        });

        function validateField(fieldToValidate){
            
            if (options['validationHandler'] == null) {
            	return true;
            }
            
            var result = options['validationHandler'](fieldToValidate);
            
            if (result && options['successHandler'] != null) {
            	options['successHandler'](fieldToValidate);	
            }
            
            if (!result && options['errorHandler'] != null) {
            	options['errorHandler'](fieldToValidate, options['errorMessage']);
            }
        }
    };
})(jQuery);

function defaultClearHandler(element) {
	$(element).keydown(function(e) {
		if (e.which != 9) {
			var submitButton = $(this).parents("form").children("input[type=submit]:first"); 
			submitButton.removeAttr('disabled'); 
			submitButton.removeClass('disabled'); 
			$(this).parents("td").next("td:last").fadeOut(); 
		}
	});
	$(element).click(function() { 
		var submitButton = $(this).parents("form").children("input[type=submit]:first"); 
		submitButton.removeAttr('disabled'); 
		submitButton.removeClass('disabled'); 
		$(this).parents("td").next("td:last").fadeOut(); 
	});
}


function defaultErrorHandler(element, message) {
	$(element).parents("td").next("td:last").fadeIn().html('<span>' + message  + '</span>'); 
	var submitButton = $(element).parents("form").children("input[type=submit]:first"); 
	submitButton.attr('disabled','true'); 
	submitButton.addClass('disabled');
}