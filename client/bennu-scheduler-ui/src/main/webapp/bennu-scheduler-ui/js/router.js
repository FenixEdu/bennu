define([ 'jquery', 'underscore', 'mustache', 'backbone', 'marionette', 'app',
		'models/Schedule', 'collections/Schedule', 'views/ScheduleList',
		'collections/Task', 'views/TaskList', 
		'views/ScheduleCreate',
		'collections/Log','models/Log', 'views/LogList', 'views/Log', 
		'collections/CustomLog','models/CustomLog', 'views/CustomLogList', 'views/AddCustomTask',
		],
		function($, _, Mustache, Backbone, Marionette, App, ScheduleModel,
				ScheduleCollection, ScheduleList, TaskCollection, TaskListView,
				ScheduleCreateView,
				LogCollection, LogModel,LogList, LogView, 
				CustomLogCollection, CustomLogModel,CustomLogList, AddCustomTaskView
				) {

			var Router = Backbone.Marionette.AppRouter.extend({

				initialize : function() {
					App.tasks = new TaskCollection();
					App.tasks.fetch({async: false,
						success : function(data) {
						}
					});
				},

				appRoutes : {
					"" : "showTasks",
					"tasks" : "showTasks",
					"schedules" : "showSchedules",
					"schedule/:type" : "prepareCreateSchedule",
					"logs" : "showLogs",
					"logs/:id" : "showLogs",
					"logs/:id/:id" : "showLogs",
					"custom/add": "addCustomTask",
					"custom": "customTasks",
					"custom/:id": "customTasks",
					"custom/:id/:id": "customTasks",
				},

				controller : {
					
					showSchedules : function() {
						var scheduleCollection = new ScheduleCollection();
						scheduleCollection.fetch({success : function() {
								App.page.show(new ScheduleList({
									collection : scheduleCollection
								}));
						}});
					},

					showTasks : function() {
						App.page.show(new TaskListView({
							collection : App.tasks
						}));
					},

					prepareCreateSchedule : function(type) {
						var task = App.tasks.where({
							type : type
						})[0];
						var scheduleModel = new ScheduleModel();
						scheduleModel.set({
							type : task.get('type'),
							name : task.get('name')
						});
						App.page.show(new ScheduleCreateView({
							model : scheduleModel
						}));
					},
					
					showLogs : function(taskName, logId) {
						
						
						if (logId) {
							 var log = new LogModel({id : logId, taskName: taskName});
							 log.fetch({success: function() {
								 App.page.show(new LogView({
									 	model : log
								 }));
							 }});
							 return;
						}
						
						var logsCollection;
						
						if (taskName) {
							logsCollection = new LogCollection([], {taskName : taskName});
						}else {
							logsCollection = new LogCollection();
						}
						
						logsCollection.fetch({success : function() {
								App.page.show(new LogList({
									collection : logsCollection
								}));
						}});
					},
					
					customTasks: function(taskName, logId) {
						var logsCollection;
						
						if (logId) {
							 var log = new CustomLogModel({taskName : taskName, id : logId});
							 log.fetch({success: function() {
								 App.page.show(new LogView({
									 	model : log
								 }));
							 }});
							 return;
						}
						
						if (taskName) {
							logsCollection = new CustomLogCollection([], {taskName : taskName});
						}else {
							logsCollection = new CustomLogCollection();
						}
						
						logsCollection.fetch({success : function() {
								App.page.show(new CustomLogList({
									collection : logsCollection,
								}));
						}});
					},
					
					addCustomTask: function() {
						App.page.show(new AddCustomTaskView());
					},
				}
			});

			return Router;

		});