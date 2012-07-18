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
    
        var elementToBind = jQuery(options['bindEventsTo']);
        
        elementToBind.bind('blur', function(){
        	var elementId = $(this).attr('id');
        	setTimeout(function(){ validateField(elementId); }, 250); 
        });
       
        elementToBind.focus(function() { 
        	options['clearHandler']($(this));
        });

        function validateField(id){
            
        	var fieldToValidate = document.getElementById(id);
            if (options['validationHandler'] == null) {
            	return true;
            }
            
            var result = options['validationHandler']($(fieldToValidate));
            
            if (result && options['successHandler'] != null) {
            	options['successHandler']($(fieldToValidate));	
            }
            
            if (!result && options['errorHandler'] != null) {
            	options['errorHandler']($(fieldToValidate), options['errorMessage']);
            }
        }
    };
})(jQuery);

function defaultClearHandler(element) {
	var submitButton = $(element).parents("form").children("input[type=submit]:first"); 
	submitButton.removeAttr('disabled'); 
	submitButton.removeClass('disabled'); 
	$(element).parents("td").next("td:last").fadeOut(300); 
}

function defaultErrorHandler(element, message) {
	$(element).parents("td").next("td:last").fadeIn(300).html('<span>' + message  + '</span>'); 
	var submitButton = $(element).parents("form").children("input[type=submit]:first"); 
	submitButton.attr('disabled','true'); 
	submitButton.addClass('disabled');
}