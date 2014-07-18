<h2>Menu Management</h2>

<link href="${pageContext.request.contextPath}/bennu-admin/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<style>
.fancytree-container {
	outline: none;
}
</style>

<div class="row">

<div class="col-lg-4">
<div id="tree">
</div>

<div><br />
	<button ng-click="saveOrder()" ng-if="changes" class="btn btn-sm btn-success">Save Changes</button>
</div>
</div>

<div class="col-lg-8" ng-show="selected">
<div class="alert alert-danger" ng-if="error">
	<strong>Error: </strong>{{error.message}}{{error.stacktrace}}
</div>
<h4>{{ selected.title | i18n }} ({{ selected.fullPath }})</h4>

<fieldset id="menu-form">
	<div class="notifications top-right"></div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="title">Title</label>
		<div class="col-sm-offset-2" ng-repeat="locale in locales">
			<input type="text" lang="{{locale.tag}}" id="title" name="title" ng-model="selected.title[locale.tag]" placeholder="Title" required> 
			<span class="add-on">{{locale.tag}}</span>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="description">Description</label>
		<div class="col-sm-offset-2" ng-repeat="locale in locales">
			<input type="text" lang="{{locale.tag}}" id="description" name="description"
				ng-model="selected.description[locale.tag]" placeholder="Description" required>
			<span class="add-on">{{locale.tag}}</span>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="path">Path</label>
		<input type="text" name="path" ng-model="selected.path"
			placeholder="Path" class="input-block-level" required ng-readonly="selected.id">
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="layout">Layout</label>
		<input type="text" name="layout" ng-model="selected.layout"
			placeholder="Layout" class="input-block-level" required>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="visible">Visible</label>
		<input type="checkbox" name="visible" ng-model="selected.visible" readonly="true">
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="accessExpression">Access Expression</label>
		<input type="text" name="accessExpression" data-group="advanced" ng-model="selected.accessExpression" placeholder="Access Expression" class="input-xlarge">
	</div>
	<div class="col-sm-offset-2">
		<button ng-click="saveSelected()" class="btn btn-success btn-sm">Save</button>
		<button data-target="#modal-delete-menu" role="button" class="btn btn-danger btn-sm" data-toggle="modal">Delete</button>
		<span ng-if="!selected.functionality && selected.id">
			<button ng-click="loadApps()" data-target="#modal-install-application" data-toggle="modal" class="btn btn-primary btn-sm">Install Application</button>
			<button ng-click="createChild()" class="btn btn-primary btn-sm">Create Child</button>
		</span>
	</div>
</fieldset>

<div id="modal-delete-menu" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
		    <div class="modal-header">
		      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		      <h3>Delete Item</h3>
		    </div>
		    <div class="modal-body">
		      <p>Do you wish to delete the selected item?</p>
		    </div>
		    <div class="modal-footer">
		      <a ng-click="deleteSelected()" data-dismiss="modal" class="btn danger">Yes</a>
		      <a data-dismiss="modal" class="btn secondary">No</a>
		    </div>
	    </div>
    </div>
</div>

<div id="modal-install-application" class="modal fade">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
		      <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		      <h3>Install Application</h3>
			</div>
			<div class="modal-body">
				<select ng-model="newApp" ng-required="true" ng-options="app.title group by app.group for app in apps | orderBy:'title' | filter: filter">
					<option value="">Select an application...</option>
				</select>
				<input class="pull-right" placeholder="Filter" ng-model="filter">
			</div>
			<div class="modal-footer">
				<button ng-click="installApplication()" data-dismiss="modal" class="btn btn-success" ng-disabled="!newApp">Install</button>
			</div>
		</div>
	</div>
</div>

</div>

</div>
