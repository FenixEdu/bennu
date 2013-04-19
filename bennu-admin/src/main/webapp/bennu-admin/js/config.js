require.config({

    paths: {
        'jquery': '../../js/libs/jquery/jquery',
        'jquery.ui': '../../js/libs/jquery/jquery-ui',
        'less': '../../js/libs/less/less-min',
        'moment': '../../js/libs/moment/moment-min',
        'underscore': '../../js/libs/underscore/underscore-min',
        'mustache': '../../js/libs/mustache/mustache-min',
        'portal': '../../bennu-portal/portal',
        'backbone': '../../js/libs/backbone/backbone',
        'marionette': '../../js/libs/backbone/backbone.marionette',
        'jquery.bootstrap': '../../js/libs/bootstrap/bootstrap-min',
        'bootstrap.notify' : '../../js/libs/bootstrap/bootstrap-notify',
        'text': '../../js/libs/require/text',
        'i18n': '../../js/libs/require/i18n',
        'appLayout': 'layouts/AppLayout',
		'bennuViews' : '../../js/views',
		'bennuTemplates' : '../../templates',
        'templates': '../templates',
    },

    config: {
        i18n: {
            locale: "en-en"
        }
    },

    shim: {
        'moment': {
            exports: 'Moment'
        },
        'underscore': {
            exports: '_'
        },
        'jquery.bootstrap': {
            deps: ['jquery'],
            exports: 'jquery'
        },
        'jquery.ui': {
            deps: ['jquery', 'bootstrap.notify'],
            exports: '$'
        },
        'backbone': {
            deps: ['underscore', 'jquery'],
            exports: 'Backbone'
        },
        'marionette': {
            deps: ['backbone'],
            exports: 'Backbone.Marionette'
        },
        
        'mustache' : {
        	exports: 'Mustache'
        },
        
        'portal' : {
        	deps: ['mustache']
        }
    },

    deps: ['main']
});
