import {Component, Input, ElementRef, OnInit, Inject} from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable }     from 'rxjs';
import {Router, ActivatedRoute} from "@angular/router";
import 'rxjs/add/operator/map';
import {IfeedService} from "../../service/ifeed.service";
import {Ifeed} from "../../model/ifeed.model";

@Component({
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    private hasPreferencesPermission: boolean;
    private ifeeds: [Ifeed];

    constructor(private http: Http,
                //elm: ElementRef,
                private route: ActivatedRoute,
                private router: Router,
                private ifeedService: IfeedService) {
        console.log("UserComponent constructor....");
    }

    ngOnInit(): void {
        this.ifeedService.setCurrentIfeedId(null);
        this.ifeedService.currentDocumentTitle = null;

        this.hasPreferencesPermission = this.ifeedService.hasPreferencesPermission;
        console.log('user ngOnInit: ' + this.ifeedService.ajaxUrl) + ', bookName=' + this.ifeedService.bookName;
        let observableResponse:Observable<Response> = this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + this.ifeedService.bookName);
        observableResponse
            .map(response => response.json())
            .subscribe(
                json => {
                    console.log('user fetched ngOnInit...' + json.ifeeds);
                    this.ifeeds = <[Ifeed]>json.ifeeds;
                    this.ifeedService.setIfeeds(<[Ifeed]>json.ifeeds);
                },
                err => {console.log(err)}
            );
    }

    getCurrentIfeedId(): string {
        return this.ifeedService.getCurrentIfeedId();
    }

    getCurrentIfeedName(): string {
        return this.ifeedService.getCurrentIfeedName();
    }

    getCurrentDocumentTitle(): string {
        return this.ifeedService.currentDocumentTitle;
    }

}