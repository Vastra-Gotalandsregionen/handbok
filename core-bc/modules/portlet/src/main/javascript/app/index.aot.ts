import { enableProdMode } from '@angular/core';
import { platformBrowser } from "@angular/platform-browser";
import { IfeedAppModuleNgFactory } from '../../../../compiled/src/main/javascript/app/app.module.ngfactory';

console.info('init... app.environment=' + app.environment);
if (app.environment === 'production') {
    enableProdMode();
}

let promise = platformBrowser().bootstrapModuleFactory(IfeedAppModuleNgFactory)
promise.then(() => {
    console.log('bootstrap fulfilled: ');
}).catch((e) => {
    console.log('bootstrap failed: ' + e);
});