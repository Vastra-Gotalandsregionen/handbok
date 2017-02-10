import {Component, ElementRef, OnInit, ViewEncapsulation} from '@angular/core';
import 'rxjs/add/operator/map';
import {IfeedService} from "./service/ifeed.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: [
        './app.component.css',
        '../../../../node_modules/@angular/material/core/theming/prebuilt/deeppurple-amber.css'
    ],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit {

    constructor(elm: ElementRef,
                private ifeedService: IfeedService) {
        ifeedService.ajaxUrl = elm.nativeElement.attributes['ajax-url'].value;
        ifeedService.hasPreferencesPermission = elm.nativeElement.attributes['has-preferences-permission'].value === 'true';
        ifeedService.bookName = elm.nativeElement.attributes['book-name'].value;

        let jwtToken = elm.nativeElement.attributes['jwt-token'].value;
        sessionStorage.setItem('jwtToken', jwtToken);
    }

    ngOnInit(): void {

    }

}