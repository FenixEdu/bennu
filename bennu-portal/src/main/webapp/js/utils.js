MenuManager.Util.renderTemplate = function(templateName, el, jsonData, callback) {
	callback = callback || function() {};
	$.get("tpl/" + templateName + ".html").success(function(data) {
		var html = Mustache.to_html(data, jsonData);
		$(el).html(html);
		callback.call();
	});
}

MenuManager.Util.parseTimestamps = function(className) {
	className = className || '.time';
	$(className).each(function() {
		$(this).text(moment($(this).text()).fromNow());
	});
}

MenuManager.Util.getHeaderView = function() {
	if(!MenuManager.DB.HeaderView) {
		MenuManager.DB.HeaderView = new MenuManager.View.HeaderView();
	}
	return MenuManager.DB.HeaderView;
}

MenuManager.Util.getFooterView = function() {
	if(!MenuManager.DB.FooterView) {
		MenuManager.DB.FooterView = new MenuManager.View.FooterView();
	}
	return MenuManager.DB.FooterView;
}

/*MenuManager.Util.getCoffeeOrderListView = function() {
	if(!MenuManager.DB.CoffeeOrderListView) {
		MenuManager.DB.CoffeeOrderListView = new MenuManager.View.CoffeeOrderListView();
	}
	return MenuManager.DB.CoffeeOrderListView;
}*/




window.utils = {

	// Asynchronously load templates located in separate .html files
	loadTemplate : function(views, callback) {

		var deferreds = [];

		$.each(views, function(index, view) {
			if (window[view]) {
				deferreds.push($.get('tpl/' + view + '.html', function(data) {
					window[view].prototype.template = Mustache.compile(data);
				}));
			} else {
				alert(view + " not found");
			}
		});

		$.when.apply(null, deferreds).done(callback);
	},

	displayValidationErrors : function(messages) {
		for (var key in messages) {
			if (messages.hasOwnProperty(key)) {
				this.addValidationError(key, messages[key]);
			}
		}
		this.showAlert('Warning!', 'Fix validation errors and try again', 'alert-warning');
	},

	addValidationError : function(field, message) {
		var controlGroup = $('#' + field).parent().parent();
		controlGroup.addClass('error');
		$('.help-inline', controlGroup).html(message);
	},

	removeValidationError : function(field) {
		var controlGroup = $('#' + field).parent().parent();
		controlGroup.removeClass('error');
		$('.help-inline', controlGroup).html('');
	},

	showAlert : function(title, text, klass) {
		$('.alert').removeClass("alert-error alert-warning alert-success alert-info");
		$('.alert').addClass(klass);
		$('.alert').html('<strong>' + title + '</strong> ' + text);
		$('.alert').show();
	},

	hideAlert : function() {
		$('.alert').hide();
	}
}; 