import {Component, Input, ElementRef, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef} from '@angular/core';
import {Http, Response} from "@angular/http";
import {Observable, Subscription}     from 'rxjs';
import {Router, ActivatedRoute} from "@angular/router";
import 'rxjs/add/operator/map';
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {IfeedService} from "../service/ifeed.service";
import {Document} from "../model/document.model";

@Component({
    selector: 'ifeed',
    templateUrl: './ifeed.component.html'
})
export class IfeedComponent implements OnInit, OnChanges {

    private sub: Subscription;
    private id: string;
    private documents: [Document] = null;
    private documentBaseUrl: string;
    private currentSubscription: Subscription = null;
    private showingDocument: boolean = false;
    private currentDocument: Document = null;

    constructor(private route: ActivatedRoute,
                private http: Http,
                private sanitizer: DomSanitizer,
                private ref: ChangeDetectorRef,
                private ifeedService: IfeedService) {
        console.log("IfeedComponent init...");
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
                this.ifeedService.setCurrentIfeedId(parseInt(this.id));

                let nameUsedForFetching = this.id;

                this.documents = null;
                this.showingDocument = false;
                this.currentDocument = null;
                this.ifeedService.currentDocumentTitle = null;

                this.ref.detectChanges();

                let subscribeToRequest: Observable<Response> = this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + this.encodeURIString(this.id) + "/document");

                let currentSubscription = subscribeToRequest
                    .map(response => response.json())
                    .subscribe(
                        json => {
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
                                    // this.documents = null;
                                    this.showingDocument = false;
                                    this.currentDocument = null;
                                    this.ifeedService.currentDocumentTitle = null;
                                }

                                this.ref.detectChanges();
                            } else {
                                console.log("Name has changed after request was made and request is therefore not relevant anymore.");
                            }

                            this.currentSubscription = null;
                        },
                        err => {
                            console.log(err)
                        }
                    );

                this.currentSubscription = currentSubscription;
            }
        });

        this.documentBaseUrl = this.ifeedService.ajaxUrl + "/document/"; // todo Kanske typ String.format för att inte kunna använda "smart id"
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
        this.ref.detectChanges();
    }

    getDocumentUrl(): SafeUrl {
        let safeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.documentBaseUrl + this.encodeURI(this.currentDocument.urlSafeUrl) + '/' + this.currentDocument.ifeedIdHmac);
        return safeResourceUrl;
    }

    backToList(): void {
        this.showingDocument = false;
        this.currentDocument = null;
        this.ifeedService.currentDocumentTitle = null;
        this.ref.detectChanges();
    }
}