<link href="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/skin-lion/ui.fancytree.css" rel="stylesheet" type="text/css">
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery-ui.min.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/bennu-admin/libs/fancytree/jquery.fancytree-all.min.js" type="text/javascript"></script>

<style>
.fancytree-container {
	outline: none;
}
#tree {
	margin-top: 9px;
}
.info {
	border-left: 1px solid #eee;
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

<div class="col-lg-8 info" ng-show="selected">
<div class="alert alert-danger" ng-if="error">
	<strong>Error: </strong>{{error.message}}{{error.stacktrace}}
</div>
<h4>{{ selected.title | i18n }} ({{ selected.fullPath }}) 
	<span class="badge pull-right">{{selected.id}}</span>
</h4>

<fieldset id="menu-form" class="form-horizontal">
	<div class="notifications top-right"></div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="title">Title</label>
		<div class="col-sm-10">
			<input type="text" bennu-localized-string="selected.title" class="form-control"/>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="description">Description</label>
		<div class="col-sm-10">
			<input type="text" bennu-localized-string="selected.description" class="form-control"/>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="path">Path</label>
		<div class="col-sm-10">
			<input type="text" name="path" ng-model="selected.path"
			placeholder="Path" class="form-control" required ng-readonly="selected.id">
		</div>
	</div>
	<div class="form-group" ng-if="selected.functionality">
		<label class="col-sm-2 control-label" for="path">Documentation Url</label>
		<div class="col-sm-10">
			<input type="text"  name="documentationUrl" ng-model="selected.documentationUrl"	placeholder="Documentation URL" class="form-control">
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="layout">Layout</label>
		<div class="col-sm-10">
			<input type="text" name="layout" ng-model="selected.layout"
				placeholder="Layout" class="form-control" required>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label" for="icon">Icon</label>
		<div class="col-sm-10">
			<input type="text" name="icon" ng-model="selected.icon"
				placeholder="Icon" class="form-control">
		</div>
	</div>
	<div class="form-group" ng-if="!selected.subRoot && !selected.appRoot">
		<label class="col-sm-2 control-label" for="visible">Visible</label>
		<div class="col-sm-10">
			<input type="checkbox" name="visible" ng-model="selected.visible" readonly="true">
		</div>
	</div>
	<div class="form-group" ng-if="!selected.subRoot && !selected.appRoot">
		<label class="col-sm-2 control-label" for="accessExpression">Access Expression</label>
		<div class="col-sm-10">
			<input type="text" name="accessExpression" data-group="advanced" ng-model="selected.accessExpression" placeholder="Access Expression" class="form-control">
		</div>
	</div>
</fieldset>
<div class="col-sm-offset-2">
	<button ng-click="saveSelected()" class="btn btn-success btn-sm" ng-if="!saving">Save</button>
	<button class="btn btn-success btn-sm" disabled ng-if="saving">Saving...</button>
	<button data-target="#modal-delete-menu" role="button" class="btn btn-danger btn-sm" data-toggle="modal">Delete</button>
	<span ng-if="!selected.functionality && selected.id">
		<button ng-click="loadApps()" data-target="#modal-install-application" data-toggle="modal" class="btn btn-primary btn-sm">Install</button>
		<button ng-click="createChild(false)" class="btn btn-primary btn-sm">Create Child</button>
		<button ng-click="createChild(true)" ng-if="selected.appRoot" class="btn btn-primary btn-sm">Create Menu</button>
	</span>
	<span ng-if="selected.functionality" class="pull-right">
		<span class="label label-default" title="Provider">{{selected.provider}}</span>
		<span class="label label-primary" title="Key">{{selected.key}}</span>
	</span>
</div>

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
		      <h4 class="modal-title">Install</h4>
			</div>
			<div class="modal-body">
				<div class="form-group">
					<label>Application</label>
					<div class="row">
						<div class="col-sm-8">
							<select ng-model="newApp" ng-required="true" ng-options="app.title group by app.group for app in apps | orderBy:'title' | filter: filter" class="form-control">
								<option value="">Select an application...</option>
							</select>
						</div>
						<div class="col-sm-4">
							<input class="form-control" placeholder="Filter" ng-model="filter">
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-4 col-sm-offset-8">
						<button ng-click="installApplication()" data-dismiss="modal" class="btn btn-success" ng-disabled="!newApp">Install Application</button>
					</div>
				</div>
				<div class="form-group" ng-show="newApp">
					<label>Functionality</label>
					<div class="row">
						<div class="col-sm-8">
							<select ng-model="newFunctionality" ng-required="true"
											ng-options="functionality.title for functionality in newApp.functionalities | orderBy: 'title'"
											class="form-control">
								<option value="">Select a functionality...</option>
							</select>
						</div>
						<div class="col-sm-4">
							<button ng-click="installFunctionality()" data-dismiss="modal" class="btn btn-success" ng-disabled="!newFunctionality">Install Functionality</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

</div>

</div>


