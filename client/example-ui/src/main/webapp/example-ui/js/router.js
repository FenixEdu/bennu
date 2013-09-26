define([ 'underscore', 'backbone', 'marionette', 'app',
		'models/Person', 'collections/People', 'views/PersonList'
		],
		function(_,  Backbone, Marionette, App,
				Person, People, PersonListView
				) {

			var Router = Backbone.Marionette.AppRouter.extend({

				appRoutes : {
					"" : "showPeople",
					"cenas": "showCenas",
				},

				controller : {
					
					showPeople : function() {
						var p1 = new Person({
			                name: "John",
			                number: 1,
			              location: "London"
			            });

			            var p2 = new Person({
			                name: "Matt",
			                number: 2,
			                location: "Dubai"
			            });

			            var p3 = new Person({
			                name: "Carl",
			                number: 3,
			                location: "New York"
			            });
						
			            var people = new People([p1,p2,p3]);
						var view = new PersonListView({collection: people});
						App.page.show(view);
					},
					
					showCenas: function() {
						App.page.show(new Backbone.Marionette.Layout({ template : "<h3>Welcome</h3>" }));
					}
					
				}
			});
			return Router;
		});