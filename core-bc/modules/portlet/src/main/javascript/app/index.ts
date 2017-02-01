import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { IfeedAppModule } from './app.module';

console.info('init... app.environment=' + app.environment);
if (app.environment === 'production') {
    enableProdMode();
}

let promise = platformBrowserDynamic().bootstrapModule(IfeedAppModule);
promise.then(function (e) {
    console.log('bootstrap fulfilled: ' + e);
}).catch(function (e) {
    console.log('bootstrap failed: ' + e);
});