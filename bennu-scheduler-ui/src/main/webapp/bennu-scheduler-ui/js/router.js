define([ 'jquery', 'underscore', 'mustache', 'backbone', 'marionette', 'app',
		'models/Schedule', 'collections/Schedule', 'views/ScheduleList',
		'collections/Task', 'views/TaskList', 
		'views/ScheduleCreate',
		'collections/Log','models/Log', 'views/LogList', 'views/Log'  
		],
		function($, _, Mustache, Backbone, Marionette, App, ScheduleModel,
				ScheduleCollection, ScheduleList, TaskCollection, TaskListView,
				ScheduleCreateView,
				LogCollection, LogModel,LogList, LogView
				) {

			var Router = Backbone.Marionette.AppRouter.extend({

				initialize : function() {
					console.log("initialize controller ...");
					App.tasks = new TaskCollection();
					App.tasks.fetch({async: false,
						success : function(data) {
							console.log("Loading tasks ...");
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
					
				},

				controller : {
					
					
					getTasks: function() {
						var tasks = new TaskCollection();
						
					},
					
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
					
					showLogs : function(id) {
						
						if (id) {
							var log = new LogModel({id : id});
							log.fetch({success: function() {
								App.page.show(new LogView({
									model : log
								}));
							}});
							return;
						}
						var logsCollection = new LogCollection();
						logsCollection.fetch({success : function() {
								App.page.show(new LogList({
									collection : logsCollection
								}));
						}});
					},
				}
			});

			Bankai.setRouter(Router);

		});