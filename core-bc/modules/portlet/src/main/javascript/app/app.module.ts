import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {AppRoutingModule} from "./app-routing.module";
import {CommonModule} from "@angular/common";
import {IfeedComponent} from "./ifeed/ifeed.component";
import {IfeedService} from "./service/ifeed.service";
import {UserComponent} from "./view/user/user.component";
import {AdminComponent} from "./view/admin/admin.component";
import {AdminGuard} from "./service/admin-guard.service";

@NgModule({
    declarations: [
        AppComponent,
        UserComponent,
        IfeedComponent,
        AdminComponent
    ],
    imports: [
        AppRoutingModule,
        BrowserModule,
        CommonModule,
        FormsModule,
        HttpModule
    ],
    providers: [
        IfeedService,
        AdminGuard
    ],
    bootstrap: [AppComponent]
})
export class IfeedAppModule {
}
