const StyleLintPlugin = require('stylelint-webpack-plugin')

module.exports = {
  transpileDependencies: [
    // can be string or regex
    // 'my-dep',
    // /other-dep/
  ],
  outputDir: 'dist',
  publicPath: process.env.NODE_ENV === 'production'
    ? `${process.env.VUE_APP_CTX ? process.env.VUE_APP_CTX : ''}/bennu-admin/`
    : '/',
  indexPath: 'index.html', // generated index file
  filenameHashing: true,
  runtimeCompiler: false,
  productionSourceMap: false,
  pages: {
    index: {
      // entry for the page
      entry: 'src/main.js',
      // the source template
      template: 'index.html',
      // output as dist/index.html
      filename: 'index.html'
    }
  },
  pluginOptions: {
    webpackBundleAnalyzer: {
      openAnalyzer: false,
      reportFilename: '../webpack-bundle-report.html'
    }
  },
  // Vue CLI sets mode to development when `npm run serve` is run and to production whith `npm run build`
  // Because of this mode should not be set here, devTool is set to source-map in production
  // Development uses a more performant option (speeds up rebuilds) that despite that correctlly maps to the source code https://webpack.js.org/configuration/devtool/
  // Docs: https://cli.vuejs.org/config/#configurewebpackc
  configureWebpack: {
    // Docs: https://webpack.js.org/configuration/optimization/
    optimization: {
      // Docs: https://webpack.js.org/plugins/split-chunks-plugin/
      // splitChunks: {
      //   chunks: 'async',
      //   minSize: 20480,
      //   maxSize: 0,
      //   minChunks: 1,
      //   maxAsyncRequests: 30,
      //   maxInitialRequests: 30,
      //   automaticNameDelimiter: '~',
      //   enforceSizeThreshold: 249856
      // }
    },
    resolve: {
      extensions: ['.vue', '.js', '.json'],
      alias: {
        '@/': './src'
      }
    },
    // Docs: https://webpack.js.org/configuration/dev-server/
    devServer: {
      host: 'localhost',
      port: 8081,
      server: 'http',
      open: true,
      client: {
        overlay: {
          warnings: true,
          errors: true
        },
        progress: true
      },
      proxy: {
        [`${process.env.VUE_APP_CTX ? process.env.VUE_APP_CTX : ''}/connect`]: {
          secure: false,
          target: 'http://localhost:8080'
        },
        [`${process.env.VUE_APP_CTX ? process.env.VUE_APP_CTX : ''}/queueing`]: {
          secure: false,
          target: 'http://localhost:8080'
        },
        [`${process.env.VUE_APP_CTX ? process.env.VUE_APP_CTX : ''}/api/bennu-core`]: {
          secure: false,
          target: 'http://localhost:8080'
        },
        // because of async selects in forms
        [`${process.env.VUE_APP_CTX ? process.env.VUE_APP_CTX : ''}/ist-admissions`]: {
          secure: false,
          target: 'http://localhost:8080'
        }
      },
      allowedHosts: ['localhost'],
      historyApiFallback: true
    }
  },
  css: {
    // globally import _variables.scss as a resource file
    loaderOptions: {

    }
  },
  chainWebpack: (config) => {
    config
      .plugin('stylelint')
      .before('vue-loader')
      .use(StyleLintPlugin, [{ files: ['src/**/*.{vue,scss}'] }])

    // if (process.env.NODE_ENV === 'production') {
    //   config
    //     .plugin('optimize-css')
    //     .tap(([options]) => {
    //       options.cssnanoOptions.preset[1].svgo = { plugins: [ { removeViewBox: false } ] }
    //       return [options]
    //     })
    // }
  }
}
