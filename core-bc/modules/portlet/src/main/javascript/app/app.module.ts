import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpModule, RequestOptions, Http} from "@angular/http";
import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {CommonModule} from "@angular/common";
import {IfeedComponent} from "./view/user/ifeed/ifeed.component";
import {GlobalStateService} from "./service/global-state.service";
import {UserComponent} from "./view/user/user.component";
import {AdminComponent} from "./view/admin/admin.component";
import {AdminGuard} from "./service/admin-guard.service";
import {DragulaModule} from "ng2-dragula";
import {TooltipModule} from "ngx-tooltip";
import {RestService} from "./service/RestService";
import {MaterialModule} from '@angular/material';
import {ErrorDialogComponent} from "./component/error-dialog/error-dialog.component";
import {ErrorHandler} from "./service/ErrorHandler";
import {AuthHttp, AuthConfig, JwtHelper} from "angular2-jwt";
import {RefreshTokenAuthHttp} from "./service/RefreshTokenAuthHttp";
import {LoadingIndicatorComponent} from "./component/loading-indicator/loading-indicator.component";
import {EditComponent} from "./view/edit/edit.component";
import {EditGuard} from "./service/edit-guard.service";
import {FeedbackButtonComponent} from "./component/feedback-button/feedback-button.component";
import {SearchDocumentsComponent} from "./component/search-documents/search-documents.component";
import {UtilityService} from "./service/utility.service";

export function authHttpServiceFactory(http: Http, options: RequestOptions, jwtHelper: JwtHelper, globalStateService: GlobalStateService) {
    return new RefreshTokenAuthHttp(new AuthConfig({
        tokenName: 'jwtToken',
        tokenGetter: (() => sessionStorage.getItem('jwtToken')),
        globalHeaders: [{'Content-Type':'application/json'}],
    }), http, options, jwtHelper, globalStateService);
}

@NgModule({
    imports: [
        AppRoutingModule,
        BrowserModule,
        CommonModule,
        DragulaModule,
        FormsModule,
        HttpModule,
        MaterialModule,
        ReactiveFormsModule,
        TooltipModule,
    ],
    declarations: [
        AppComponent,
        EditComponent,
        UserComponent,
        IfeedComponent,
        AdminComponent,
        ErrorDialogComponent,
        FeedbackButtonComponent,
        LoadingIndicatorComponent,
        SearchDocumentsComponent
    ],
    providers: [
        AdminGuard,
        EditGuard,
        ErrorHandler,
        GlobalStateService,
        JwtHelper,
        RestService,
        SearchDocumentsComponent,
        ErrorDialogComponent,
        UtilityService,
        {
            provide: AuthHttp,
            useFactory: authHttpServiceFactory,
            deps: [Http, RequestOptions, JwtHelper, GlobalStateService]
        }
    ],
    entryComponents: [ErrorDialogComponent, SearchDocumentsComponent],
    bootstrap: [AppComponent]
})
export class IfeedAppModule {
}
