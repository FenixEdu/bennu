(function(globals, $) {

	function getCookieValue(key) {
		var cookies = document.cookie.split('; ');
		for (var i = 0, parts; (parts = cookies[i] && cookies[i].split('=')); i++) {
			if (decode(parts.shift()) === key) {
				return decode(parts.join('='));
			}
		}
		return null;
	}

	function decode(s) {
		return decodeURIComponent(s.replace(/\+/g, ' '));
	}

	var contextPath = getCookieValue("contextPath") || "/";

	var groupEditorTemplate = [
		'<div class="bennu-group-editor">',
			'<div class="bennu-group-description">',
			'</div>',
			'<div class="bennu-form-container">',
			'</div>',
			'<div class="bennu-group-editor-actions">',
				'<button class="bennu-group-editor-action" data-action="grant">Grant</button>',
				'<button class="bennu-group-editor-action" data-action="revoke">Revoke</button>',
			'</div>',
		'</div>'].join('');

	var operationUserForm = [
		'<input type="text" class="username-textbox" placeholder="Username" />',
		'<button class="execute-action"></button>'
	].join('');

	var operationMap = {
		"grant": "Grant",
		"revoke": "Revoke"
	};

	var getUrl = function(relativePath) {
		return contextPath+relativePath;
	};

	var bindGroupEditor = function(el, data) {
		var groupEditor = $(groupEditorTemplate).insertAfter(el);
		var advancedMode = $(el).data("group") === "advanced";
		if(!advancedMode) {
			$(el).hide();
		}
		$(el).data("name", data.name);
		$(el).val(data.expression);

		$(groupEditor).find(".bennu-group-description").html(data.name);

		$(".bennu-group-editor-action", groupEditor).click(function(e) {
			var operation = $(this).data("action");
			$(".bennu-group-editor-actions", groupEditor).hide();
			$(".bennu-form-container", groupEditor).append(operationUserForm);
			$(".execute-action", groupEditor).html(operationMap[operation]);
			$(".execute-action", groupEditor).click(function(e) {
				var username = $(".username-textbox", groupEditor).val();
				$.getJSON(getUrl("/api/bennu-core/groups/"+operation), {
					username: username,
					groupExpression: $(el).val()
				}, function(newData) {
					groupEditor.remove();
					bindGroupEditor(el, newData);
				});
			});
		});

	};

	var loadAndBindGroupEditors = function() {
		$("input[data-group]").each(function(idx, el) {
			var groupExpression = $(el).val();
			$.getJSON(getUrl("/api/bennu-core/groups"), { groupExpression: groupExpression }, function(data) {
				bindGroupEditor(el, data);
			});
		});
	};

	var checkIfGroupEditorsCanBeBinded = function() {
		var groupEditors = $("input[data-group]");
		if(groupEditors.length > 0 && $(groupEditors[0]).val() !== "") {
			loadAndBindGroupEditors();
		} else {
			//WHAT IS WRONG WITH ME: hack to wait for mr. angular.js to bind data to the view
			setTimeout(function() {
				loadAndBindGroupEditors();
			}, 500);
		}
	};

	checkIfGroupEditorsCanBeBinded();

})(window, $);