import {Component, Input, ElementRef, OnInit, Inject} from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable }     from 'rxjs';
import {Router} from "@angular/router";
import 'rxjs/add/operator/map';
import {IfeedService} from "../../service/ifeed.service";

@Component({
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.css']
})
export class AdminComponent {


    constructor(private http: Http, private ifeedService: IfeedService) {
    }

    post(): void {
        let subscribeToRequest:Observable<Response> = this.http.post(this.ifeedService.ajaxUrl + "/ifeed...", "?data={'key':'value'}");

        let currentSubscription = subscribeToRequest
            .map(response => response.json())
            .subscribe(
                json => {

                    console.log(json);


                },
                err => {console.log(err)}
            );

    }
}