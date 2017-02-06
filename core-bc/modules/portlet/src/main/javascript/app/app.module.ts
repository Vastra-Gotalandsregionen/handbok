import {BrowserModule} from "@angular/platform-browser";
import {NgModule} from "@angular/core";
import {FormsModule} from "@angular/forms";
import {HttpModule} from "@angular/http";
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
// import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {RestService} from "./service/RestService";
import {ModalModule} from "angular2-modal";
import {Modal, BootstrapModalModule} from "angular2-modal/plugins/bootstrap";
import {ErrorHandler} from "./service/ErrorHandler";
import {CustomModal} from "./service/CustomModalContext";

@NgModule({
    declarations: [
        AppComponent,
        UserComponent,
        IfeedComponent,
        AdminComponent,
        CustomModal
    ],
    imports: [
        AppRoutingModule,
        BrowserModule,
        CommonModule,
        DragulaModule,
        FormsModule,
        HttpModule,
        ModalModule.forRoot(),
        BootstrapModalModule,
        // NgbModule.forRoot(),
        TooltipModule
    ],
    providers: [
        AdminGuard,
        ErrorHandler,
        IfeedService,
        RestService,
        Modal
    ],
    entryComponents: [CustomModal],
    bootstrap: [AppComponent]
})
export class IfeedAppModule {
}
