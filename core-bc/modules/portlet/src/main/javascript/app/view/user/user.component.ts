import {Component, OnInit} from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable }     from 'rxjs';
import 'rxjs/add/operator/map';
import {IfeedService} from "../../service/ifeed.service";
import {Ifeed} from "../../model/ifeed.model";
import {ErrorHandler} from "../../service/ErrorHandler";

@Component({
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    hasAdminPermission: boolean;
    ifeeds: [Ifeed];

    constructor(private http: Http,
                private ifeedService: IfeedService,
                private errorHandler: ErrorHandler) {
    }

    ngOnInit(): void {
        this.ifeedService.setCurrentIfeedId(null);
        this.ifeedService.currentDocumentTitle = null;

        this.hasAdminPermission = this.ifeedService.hasAdminPermission;
        let observableResponse:Observable<Response> = this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + this.ifeedService.bookName);
        observableResponse
            .map(response => response.json())
            .subscribe(
                json => {
                    this.ifeeds = <[Ifeed]>json.ifeeds;
                    this.ifeedService.setIfeeds(<[Ifeed]>json.ifeeds);
                },
                err => {
                    this.errorHandler.notifyError(err);
                }
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

    getBookName(): string {
        return this.ifeedService.bookName;
    }

}