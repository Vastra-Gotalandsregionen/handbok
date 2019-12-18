import {Component, ElementRef, OnInit, ViewEncapsulation} from '@angular/core';
import 'rxjs/add/operator/map';
import {GlobalStateService} from "./service/global-state.service";
import {NavigationEnd, Router} from "@angular/router";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: [
        './app.component.css',
        '../../../../node_modules/@angular/material/prebuilt-themes/deeppurple-amber.css'
    ],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent {

    constructor(elm: ElementRef,
                router: Router,
                private globalStateService: GlobalStateService) {

        globalStateService.bookName = elm.nativeElement.attributes['book-name'].value;

        globalStateService.ajaxUrl = elm.nativeElement.attributes['ajax-url'].value;
        globalStateService.resourceUrl = elm.nativeElement.attributes['resource-url'].value;
        globalStateService.hasAdminPermission = elm.nativeElement.attributes['has-admin-permission'].value === 'true';
        globalStateService.bookName = elm.nativeElement.attributes['book-name'].value;
        globalStateService.bookId = elm.nativeElement.attributes['book-id'].value;

        let jwtToken = elm.nativeElement.attributes['jwt-token'].value;
        sessionStorage.setItem('jwtToken', jwtToken);

        let editModeAttribute = elm.nativeElement.attributes['edit-mode'];
        if (editModeAttribute && editModeAttribute.value === "true") {
            globalStateService.hasEditPermission = editModeAttribute.value === 'true';
            globalStateService.portletResourcePk = elm.nativeElement.attributes['portlet-resource-pk'].value;

            router.navigate(['/edit']);
        }

        let isIE: boolean = !!navigator.userAgent.match(/Trident/g) || !!navigator.userAgent.match(/MSIE/g);

        globalStateService.isIE = isIE;

        router.events.subscribe( (event) => {

            // Add page to cache so it can be loaded offline if hitting the address directly in browser.
            if (event instanceof NavigationEnd) {
                // Hide loading indicator
                caches.keys().then((keys: string[]) => {
                    keys.filter((k: string) => k.indexOf('handbok') > -1).forEach(key => {
                        caches.open(key).then(function (cache) {
                            cache.add(document.location.href);
                            // console.log('added: ' + document.location.href);
                        });
                    });
                });
            }
        });

    }
}
