import {Component, ElementRef, OnInit, ViewEncapsulation} from '@angular/core';
import 'rxjs/add/operator/map';
import {IfeedService} from "./service/ifeed.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: [
        './app.component.css',
        '../../../../node_modules/@angular/material/core/theming/prebuilt/deeppurple-amber.css'
    ],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent {

    constructor(elm: ElementRef,
                router: Router,
                private ifeedService: IfeedService) {

        ifeedService.ajaxUrl = elm.nativeElement.attributes['ajax-url'].value;
        ifeedService.resourceUrl = elm.nativeElement.attributes['resource-url'].value;
        ifeedService.hasAdminPermission = elm.nativeElement.attributes['has-admin-permission'].value === 'true';
        ifeedService.bookName = elm.nativeElement.attributes['book-name'].value;

        let jwtToken = elm.nativeElement.attributes['jwt-token'].value;
        sessionStorage.setItem('jwtToken', jwtToken);

        let editModeAttribute = elm.nativeElement.attributes['edit-mode'];
        if (editModeAttribute && editModeAttribute.value === "true") {
            ifeedService.hasEditPermission = editModeAttribute.value === 'true';
            ifeedService.portletResourcePk = elm.nativeElement.attributes['portlet-resource-pk'].value;

            router.navigate(['/edit']);
        }

    }
}