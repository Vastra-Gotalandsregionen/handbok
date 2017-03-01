import {Component, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef} from '@angular/core';
import {Response} from "@angular/http";
import {Observable, Subscription}     from 'rxjs';
import {ActivatedRoute} from "@angular/router";
import 'rxjs/add/operator/map';
import {DomSanitizer, SafeUrl, SafeResourceUrl} from "@angular/platform-browser";
import {IfeedService} from "../../../service/ifeed.service";
import {Document} from "../../../model/document.model";
import {RestService} from "../../../service/RestService";
import {ErrorHandler} from "../../../service/ErrorHandler";
import {UtilityService} from "../../../service/utility.service";

@Component({
    selector: 'ifeed',
    styleUrls: ['./ifeed.component.css'],
    templateUrl: './ifeed.component.html'
})
export class IfeedComponent implements OnInit, OnChanges {

    sub: Subscription;
    id: string;
    documents: [Document] = null;
    documentBaseUrl: string;
    currentSubscription: Subscription = null;
    showingDocument: boolean = false;
    currentDocument: Document = null;
    documentUrl: SafeResourceUrl;
    mobileBrowser: boolean;

    constructor(private route: ActivatedRoute,
                private sanitizer: DomSanitizer,
                private ref: ChangeDetectorRef,
                private ifeedService: IfeedService,
                private restService: RestService,
                private errorHandler: ErrorHandler,
                private utilityService: UtilityService) {
        this.mobileBrowser = this.utilityService.mobileAndTabletCheck();
    }

    ngOnInit(): void {

        console.log("params: " + this.route.params);
        console.log("queryparams: " + this.route.queryParams);

        let urlSafeUrl: string = null;
        let ifeedIdHmac: string = null;

        let queryParamSubscription = this.route.queryParams.subscribe(params => {
            urlSafeUrl = params['urlSafeUrl'] || null;
            ifeedIdHmac = params['ifeedIdHmac'] || null;

            let match: boolean = false;
            if (urlSafeUrl !== null && this.documents !== null) {
                for (let document of this.documents) {
                    if (document.urlSafeUrl === urlSafeUrl) {
                        match = true;
                        this.showDocument(document);
                    }
                }
            }

            if (!match) {
                this.showingDocument = false;
                this.currentDocument = null;
                this.ifeedService.currentDocumentTitle = null;
            }
        });

        this.sub = this.route.params.subscribe(params => {
            if (this.currentSubscription != null) {
                this.currentSubscription.unsubscribe();
                this.currentSubscription = null;
                console.log("Abort request.");
            }

            if (this.id !== params['id']) {
                this.id = params['id'] || '';
                this.ifeedService.setCurrentIfeedId(this.id);

                let nameUsedForFetching = this.id;

                let subscribeToRequest: Observable<Response> = this.restService.getDocumentsForIfeed(this.id);

                let timerSubscription: Subscription = Observable.timer(250).subscribe(undefined, undefined, () => {
                    this.documents = null;
                    this.showingDocument = false;
                    this.ifeedService.currentDocumentTitle = null;
                    this.documents = null;
                });

                let currentSubscription = subscribeToRequest
                    .map(response => response.json())
                    .subscribe(
                        json => {
                            timerSubscription.unsubscribe();

                            // Check if if we got the response for the current request and that the client hasn't already requested another feed.
                            if (nameUsedForFetching === this.id) {
                                this.documents = <[Document]>json;

                                let match: boolean = false;

                                if (urlSafeUrl !== null) {
                                    for (let document of this.documents) {
                                        if (document.urlSafeUrl === urlSafeUrl) {
                                            match = true;
                                            this.showDocument(document);
                                        }
                                    }
                                }

                                if (!match) {
                                    this.showingDocument = false;
                                    this.currentDocument = null;
                                    this.ifeedService.currentDocumentTitle = null;
                                }

                            } else {
                                console.log("Name has changed after request was made and request is therefore not relevant anymore.");
                            }

                            this.currentSubscription = null;
                        },
                        err => {
                            timerSubscription.unsubscribe();

                            this.errorHandler.notifyError(err);

                            this.documents = null;
                            this.showingDocument = false;
                            this.currentDocument = null;
                            this.ifeedService.currentDocumentTitle = null;
                        }
                    );

                this.currentSubscription = currentSubscription;
            }
        });

        this.documentBaseUrl = this.ifeedService.ajaxUrl + "/document/";
    }

    ngOnChanges(changes: SimpleChanges): void {
        console.log('ngOnChanges: ' + changes);
    }

    encodeURIString(input: String): string {
        return encodeURIComponent(input.toString());
    }

    encodeURI(input: string): string {
        return encodeURIComponent(input);
    }

    showDocument(document: Document): void {
        this.showingDocument = true;
        this.currentDocument = document;
        this.ifeedService.currentDocumentTitle = document.title;

        if (!this.mobileBrowser) {
            let safeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.documentBaseUrl + this.encodeURI(this.currentDocument.urlSafeUrl) + '/' + this.currentDocument.ifeedIdHmac);

            this.documentUrl = safeResourceUrl;
        } else {
            window.location.href = this.getDocumentUrl();
        }

        this.showingDocument = true;
    }

    openInNewPage(document: Document, event: Event): boolean {
        this.currentDocument = document;
        window.location.href = this.getDocumentUrl();
        return false;
    }

    getSafeDocumentUrl(): SafeUrl {
        return this.documentUrl;
    }

    backToList(): void {
        this.showingDocument = false;
        this.currentDocument = null;
        this.ifeedService.currentDocumentTitle = null;
    }

    getDocumentUrl(): string {
        return this.documentBaseUrl + this.encodeURI(this.currentDocument.urlSafeUrl) + '/' + this.currentDocument.ifeedIdHmac;
    }
}