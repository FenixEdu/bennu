<html ng-app="bootstrapModule" ng-controller="BootstrapCtrl">

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<script> window.ctxPath = '${pageContext.request.contextPath}'; </script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-core/js/angular.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-core/js/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/bennu-core/js/bootstrap.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bennu-core/css/bootstrap.min.css"></link>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/bennu-core/css/bootstrapWizard.css"></link>
	<title>FenixEdu - Bootstrap</title>
</head>
<div class="container">

	<div>
		<div ng-show="!locale">
			<form>
				<div class="row">
					<div class="col-lg-10 col-sm-12">
						<h1>Bootstrap</h1>
					</div>
					<div class="col-lg-2">
						<h1><img src="${pageContext.request.contextPath}/img/logo_bennu.svg" alt="Bennu Logo" width="200px" class="pull-right" /></h1>
					</div>
				</div>
				<fieldset>
					<legend>Welcome<span class="badge">1</span></legend>
					
					<div>Before you can bootstrap your application we need some info.</div>
					
					<br>
					
					<label for="input" class="col-sm-2 control-label">Wizard language:</label>
					<div class="col-sm-10">
						<select name="input" class="form-control" ng-required="{{field.isMandatory}}"
							ng-model="tempLocale" ng-options="l.name for l in availableLocales">
							<option value="">Choose One</option>
						</select>
					</div>
				</fieldset>

				<ul class="pager">
					<li  ng-class="{disabled: !tempLocale}" class="next">
						<a href="#" ng-click="submitInitialConfig()">Next</a>
					</li>
				</ul>
			</form>
		</div>

		<form ng-submit="submitWizard()" novalidate class="form-horizontal" role="form"  ng-show="locale && !submitting" ng-repeat="bootstrapper in bootstrappers">
			<ng-form ng-repeat="section in bootstrapper.sections">
				<section class="wizardSection" ng-show="getCurrentStep() == section">
					<div class="row">
						<div class="col-lg-10 col-sm-12">
							<h1>{{bootstrapper.name[locale.key]}}</h1>
						</div>
						<div class="col-lg-2">
							<h1><img src="${pageContext.request.contextPath}/img/logo_bennu.svg" width="200px" class="pull-right" /></h1>
						</div>
					</div>
				
					<fieldset>
						<legend>{{section.name[locale.key]}}<span class="badge">{{currentSectionNumber + 2}}</span></legend>
						
						<div>{{section.description[locale.key]}}</div>

						<br/>
						
						<div ng-show="errors" ng-repeat="error in errors">
							<div ng-hide="error.fieldKey" class="global-error">{{error.message[locale.key]}}</div>
						</div>

						<div ng-repeat="field in section.fields" class="form-group">
							<ng-form class="row" ng-class="{'has-error': field.hasError}">
								<label for="input" class="col-sm-2 control-label">{{field.name[locale.key]}}:</label>

								<div class="col-sm-9">
									<input name="input" class="form-control" type="{{field.fieldType}}" 
										placeholder="{{field.hint[locale.key]}}" ng-required="{{field.isMandatory}}" 
										ng-show="{{field.validValues.length == 0}}" ng-model="field.value"
										ng-keyup="$event.keyCode == 13 ? advance() : null" />

									<select name="input" class="form-control" ng-required="{{field.isMandatory}}" 
										ng-show="{{field.validValues > 0}}" ng-model="field.value" 
										ng-options="v for v in field.validValues">
										<option value="">Choose One</option>
									</select>
									
									<div class="help-block" ng-show="field.hasRequiredError">This field is required.</div>
									<div class="help-block" ng-show="field.hasError && field.error">{{field.error.message[locale.key]}}</div>
								</div>
							</ng-form>
						</div>
					</fieldset>

					<ul class="pager">
						<li class="previous" ng-show="$first && firstBootstrapper()">
							<a href="#" ng-click="showInitialConfig()" tabindex="-99">Previous</a>
						</li>
						<li class="previous" ng-hide="$first && firstBootstrapper()">
							<a href="#" ng-click="previousStep()" tabindex="-99">Previous</a>
						</li>
						<li class="next" ng-hide="$last && lastBootstrapper()">
							<a href="#" ng-click="nextStep()">Next</a>
						</li>
						<li ng-class="{disabled: !$last}" class="next" ng-show="$last && lastBootstrapper()">
							<a href="#" ng-click="submitWizard()" class="primary">Submit</a>
						</li>
					</ul>
				</section>
			</ng-form>
		</form>

		<div ng-if="submitting">
			<div class="row">
				<div class="col-lg-10 col-sm-12">
					<h1>Bootstrap</h1>
				</div>
				<div class="col-lg-2">
					<h1><img src="${pageContext.request.contextPath}/img/logo_bennu.svg" alt="Bennu Logo" width="200px" class="pull-right" /></h1>
				</div>
				<hr />
			</div>
			<fieldset>
				<legend>Finish</legend>

				<h4 ng-if="!exception" class="text-center">Configuring your installation, please wait...</h4>

				<div ng-if="exception">
					<h4 ng-click="showException = !showException" class="text-center">An error ocurred, could not configure your installation :(</h4>
					<pre ng-if="showException">{{exception.stacktrace}}</pre>
				</div>
			</fieldset>

			<ul class="pager">
			</ul>
		</div>
	</div>
</div>

</html>