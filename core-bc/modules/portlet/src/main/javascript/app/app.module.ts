import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule, RequestOptions, Http} from "@angular/http";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {CommonModule} from "@angular/common";
import {IfeedComponent} from "./ifeed/ifeed.component";
import {IfeedService} from "./service/ifeed.service";
import {UserComponent} from "./view/user/user.component";
import {AdminComponent} from "./view/admin/admin.component";
import {AdminGuard} from "./service/admin-guard.service";
import {DragulaModule} from "ng2-dragula";
import {TooltipModule} from "ngx-tooltip";
import {RestService} from "./service/RestService";
import {MaterialModule} from '@angular/material';
import {ErrorDialogComponent} from "./component/error-dialog.component";
import {ErrorHandler} from "./service/ErrorHandler";
import {AuthHttp, AuthConfig} from "angular2-jwt";

function authHttpServiceFactory(http: Http, options: RequestOptions) {
    return new AuthHttp(new AuthConfig({
        tokenName: 'jwtToken',
        tokenGetter: (() => sessionStorage.getItem('jwtToken')),
        globalHeaders: [{'Content-Type':'application/json'}],
    }), http, options);
}

@NgModule({
    imports: [
        AppRoutingModule,
        BrowserModule,
        CommonModule,
        DragulaModule,
        FormsModule,
        HttpModule,
        MaterialModule.forRoot(),
        TooltipModule
    ],
    declarations: [
        AppComponent,
        UserComponent,
        IfeedComponent,
        AdminComponent,
        ErrorDialogComponent
    ],
    providers: [
        AdminGuard,
        ErrorHandler,
        IfeedService,
        RestService,
        ErrorDialogComponent,
        {
            provide: AuthHttp,
            useFactory: authHttpServiceFactory,
            deps: [Http, RequestOptions]
        }
    ],
    entryComponents: [ErrorDialogComponent],
    bootstrap: [AppComponent]
})
export class IfeedAppModule {
}
