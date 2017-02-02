import {Component, Input, ElementRef, OnInit, Inject, ViewEncapsulation} from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable }     from 'rxjs';
import {Router} from "@angular/router";
import 'rxjs/add/operator/map';
import {IfeedService} from "./service/ifeed.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    encapsulation: ViewEncapsulation.None
})
export class AppComponent implements OnInit {

    constructor(private http: Http,
                elm: ElementRef,
                private router: Router,
                private ifeedService: IfeedService) {
        console.log("AppComponent constructor....");
        ifeedService.ajaxUrl = elm.nativeElement.attributes['ajax-url'].value;
        ifeedService.hasPreferencesPermission = elm.nativeElement.attributes['has-preferences-permission'].value === 'true';
        ifeedService.bookName = elm.nativeElement.attributes['book-name'].value;
    }

    ngOnInit(): void {

    }

}