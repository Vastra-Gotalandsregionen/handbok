var webpack = require('webpack');
var HtmlWebpackPlugin = require('html-webpack-plugin');
const prod = process.argv.indexOf('-p') !== -1;

module.exports = {

    entry: {
        'app': './src/main/javascript/app/index.ts',
        'polyfills': [
            './node_modules/core-js/es6',
            './node_modules/core-js/es7/reflect',
            './node_modules/zone.js/dist/zone'
        ]
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
            {
                test: /\.ts$/,
                loaders: ['angular2-template-loader', 'awesome-typescript-loader']
            },
            {test: /\.html$/, loader: 'raw-loader'},
            {test: /\.css$/, loader: 'raw-loader'}
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
        })/**,
        new webpack.optimize.UglifyJsPlugin({
            compress: { warnings: true },
            comments: false,
            mangle: true
        })*/
    ]

};
