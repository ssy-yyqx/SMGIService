const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  configureWebpack: {
    // provide the app's title in webpack's name field, so that
    // it can be accessed in index.html to inject the correct title.
    resolve: {
      fallback: { path: require.resolve("path-browserify") },
    },
  },
  transpileDependencies: true,
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: false,
  productionSourceMap: false,
  devServer: {
    port: 8090,
    host: '192.168.1.14',
    https: false,
    open: false,
    // open: true,
    // overlay: {
    //   warnings: false,
    //   errors: true
    // },
    proxy: {
      '^/dev-api': {
        target: 'http://192.168.1.14:10003/',
        changeOrigin: false,
        pathRewrite: {
          '^/dev-api': ''
        }
      },
      '^/nt-api': {
        target: 'ws://192.168.1.14:19999/',
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/ws-api': ''
        }
      }
    }
  }
})
