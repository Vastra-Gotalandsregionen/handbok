var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
var path = require('path');
var CompressionPlugin = require("compression-webpack-plugin");

const prod = process.argv.indexOf('-p') !== -1;
const aot = process.env.aot === "true";
const ngcWebpack = require('ngc-webpack');
const helpers = require('./helpers');
// var css = require("!css-loader!sass-loader");
// var css = require("./file.scss");
// var css = require("!raw!sass");
// const aot = helpers.hasNpmFlag('aot');
console.log('aot:' + aot);
// console.log('path:' + path.resolve(__dirname, 'src/main/javascript/app/index.aot.ts'));
module.exports = {

    entry: {
        'app': './src/main/javascript/app/index' + (aot ? '.aot' : '') + '.ts',
        'polyfills': [
            './node_modules/core-js/es6',
            './node_modules/core-js/es7/reflect',
            './node_modules/zone.js/dist/zone'
        ]/**,
        'style': './src/main/sass/app.scss'*/
    },
    output: {
        path: './src/main/webapp',
        filename: 'js/[name].js'
        /**filename: 'asset/[name].[hash].js'*/
    },
    module: {
        loaders: [
            /**{test: /\.component.ts$/, loader: 'ts-loader!angular2-template-loader'},
            {test: /\.ts$/, exclude: /\.component.ts$/, loader: 'ts-loader'},*/
            /** { test: /.*\.aot/, loader: 'ignore-loader' },*/
            {
                test: /.*\.ts$/, loaders: ['angular2-template-loader', 'awesome-typescript-loader']
            },
            {test: /\.html$/, loader: 'raw-loader'},
            {test: /\.css$/, loader: 'raw-loader'},/**,
            {
                test: /\.scss$/,
                loaders: ["style-loader", "css-loader", "sass-loader"]
            }*/
        ]
    },
    resolve: {
        extensions: ['.js', '.ts', '.html', '.css']
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            name: 'polyfills'
        }),
        /**new HtmlWebpackPlugin({
            template: './src/main/webapp/index.html'/!**,
            path: './src/main/webapp/',
            output: './src/main/webapp/index.generated.html'*!/
        }),*/
        new webpack.DefinePlugin({
            app: {
                /**environment: JSON.stringify(process.env.APP_ENVIRONMENT || 'development')*/
                environment: prod? '"production"': '"development"'
            }
        }),
        new CompressionPlugin({
            asset: "[path].gz[query]",
            algorithm: "gzip",
            test: /\.js$|\.html$/,
            threshold: 10240,
            minRatio: 0.8
        })
    ]
};

if (aot) {
    module.exports.plugins.push(
        new ngcWebpack.NgcWebpackPlugin({
            tsConfig: 'tsconfig.webpack.json'
        })
    );
}

if (prod) {
    module.exports.plugins.push(
        new webpack.optimize.UglifyJsPlugin({
            compress: { warnings: false },
            comments: false,
            mangle: true
        })
    );
}
