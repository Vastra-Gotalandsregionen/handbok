import {Component, OnInit} from '@angular/core';
import {Http, Response} from "@angular/http";
import { Observable }     from 'rxjs';
import 'rxjs/add/operator/map';
import {GlobalStateService} from "../../service/global-state.service";
import {Ifeed} from "../../model/ifeed.model";
import {ErrorHandler} from "../../service/ErrorHandler";
import {Router} from "@angular/router";

@Component({
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    hasAdminPermission: boolean;
    ifeeds: [Ifeed];
    needsConfiguration: boolean;

    constructor(private http: Http,
                private globalStateService: GlobalStateService,
                private errorHandler: ErrorHandler,
                private router: Router) {
    }

    ngOnInit(): void {
        this.needsConfiguration = !(this.globalStateService.bookName && this.globalStateService.bookName.length > 0);

        this.globalStateService.setCurrentIfeedId(null);
        this.globalStateService.currentDocumentTitle = null;

        this.hasAdminPermission = this.globalStateService.hasAdminPermission;
        let observableResponse:Observable<Response> = this.http.get(this.globalStateService.ajaxUrl + "/ifeed/" + this.globalStateService.bookId);
        observableResponse
            .map(response => response.json())
            .subscribe(
                json => {
                    this.ifeeds = <[Ifeed]>json.ifeeds;
                    this.globalStateService.setIfeeds(<[Ifeed]>json.ifeeds);
                },
                err => {
                    this.errorHandler.notifyError(err);
                }
            );
    }

    getCurrentIfeedId(): string {
        return this.globalStateService.getCurrentIfeedId();
    }

    getCurrentIfeedName(): string {
        return this.globalStateService.getCurrentIfeedName();
    }

    getCurrentDocumentTitle(): string {
        return this.globalStateService.currentDocumentTitle;
    }

    getBookName(): string {
        return this.globalStateService.bookName;
    }

    goToIfeed(ifeedId: string): boolean {
        this.globalStateService.resetDocument();

        setTimeout(() => {
            this.router.navigate(['/user/ifeed/' + ifeedId]);
        });

        // Like preventDefault().
        return false;
    }
}