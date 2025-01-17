const { defineConfig } = require('@vue/cli-service');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = defineConfig({
  transpileDependencies: true,
  publicPath: process.env.BASE_URL || '/',

  chainWebpack: (config) => {
    // Remove default HtmlWebpackPlugin
    config.plugins.delete('html');
    config.plugins.delete('preload');
    config.plugins.delete('prefetch');

    // Reconfigure HtmlWebpackPlugin
    config.plugin('html').use(HtmlWebpackPlugin, [
      {
        template: './public/index.html',
        inject: true,
        BASE_URL: process.env.BASE_URL || '/',
      },
    ]);
  },
});
