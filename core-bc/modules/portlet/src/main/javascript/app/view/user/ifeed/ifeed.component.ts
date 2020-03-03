import {Component, OnInit, OnChanges, SimpleChanges, ChangeDetectorRef, Input} from '@angular/core';
import {Response} from "@angular/http";
import {Observable, Subscription}     from 'rxjs';
import {ActivatedRoute, CanDeactivate, Router} from "@angular/router";
import 'rxjs/add/operator/map';
import {DomSanitizer, SafeUrl, SafeResourceUrl} from "@angular/platform-browser";
import {GlobalStateService} from "../../../service/global-state.service";
import {Document} from "../../../model/document.model";
import {RestService} from "../../../service/RestService";
import {ErrorHandler} from "../../../service/ErrorHandler";
import {UtilityService} from "../../../service/utility.service";
import {Ifeed} from "../../../model/ifeed.model";
import {CacheService} from "../../../service/cache.service";

@Component({
    selector: 'ifeed',
    styleUrls: ['./ifeed.component.css'],
    templateUrl: './ifeed.component.html'
})
export class IfeedComponent implements OnInit, OnChanges {

    sub: Subscription;
    id: string;
    documents: Document[] = null;
    ifeed: Ifeed;
    documentBaseUrl: string;
    currentSubscription: Subscription = null;
    showingDocument: boolean = false;
    currentDocument: Document = null;
    documentUrl: SafeResourceUrl;
    mobileBrowser: boolean;

    resetDocumentObservable$: Observable<void>;
    documentsCached = {};

    constructor(private route: ActivatedRoute,
                private router: Router,
                private sanitizer: DomSanitizer,
                private ref: ChangeDetectorRef,
                public globalStateService: GlobalStateService,
                private restService: RestService,
                private errorHandler: ErrorHandler,
                private utilityService: UtilityService,
                private cacheService: CacheService) {
        this.mobileBrowser = this.utilityService.mobileAndTabletCheck();
    }

    ngOnInit(): void {

        this.resetDocumentObservable$ = this.globalStateService.resetDocumentObservable$;

        // Construct to mitigate strange behavior in IE where view turns all white.
        this.resetDocumentObservable$.subscribe(() => {
            this.resetIframeUrl();
        });

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
                this.globalStateService.currentDocumentTitle = null;
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
                this.globalStateService.setCurrentIfeedId(this.id);

                let nameUsedForFetching = this.id;

                let subscribeToRequest: Observable<Response> = this.restService.getDocumentsForIfeed(this.id);
                let subscribeToIfeedRequest: Observable<Response> = this.restService.getIfeed(this.id);

                let timerSubscription: Subscription = Observable.timer(250).subscribe(undefined, undefined, () => {
                    this.showingDocument = false;
                    this.globalStateService.currentDocumentTitle = null;
                    this.documents = null;
                });

                let currentSubscription = Observable.forkJoin(subscribeToRequest, subscribeToIfeedRequest)
                    .subscribe(join => {
                        timerSubscription.unsubscribe();

                        // Check if if we got the response for the current request and that the client hasn't already requested another feed.
                        if (nameUsedForFetching === this.id) {
                            this.documents = <[Document]>join[0].json();
                            this.ifeed = <Ifeed>join[1].json();

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
                                this.globalStateService.currentDocumentTitle = null;
                            }

                            this.updateDocumentsCacheStatus();
                        } else {
                            console.log("Name has changed after request was made and request is therefore not relevant anymore.");
                        }

                        this.currentSubscription = null;
                    },
                    err => {
                        timerSubscription.unsubscribe();

                        this.errorHandler.notifyError(err);

                        this.documents = [];
                        this.showingDocument = false;
                        this.currentDocument = null;
                        this.globalStateService.currentDocumentTitle = null;
                    }
                );

                this.currentSubscription = currentSubscription;
            }
        });

        this.documentBaseUrl = this.globalStateService.ajaxUrl + "/document/";

        this.cacheService.cacheStatusEvent$.subscribe(ifeedId => {
            if (this.ifeed.id === ifeedId) {
                this.updateDocumentsCacheStatus();
            }
        });

        this.globalStateService.getCachingEnabled().subscribe(enabled => this.updateDocumentsCacheStatus());
    }

    private updateDocumentsCacheStatus() {
        const documentsCachedTemp = {};
        const promises = <Promise<any>[]>[];

        this.cacheService.getCache().subscribe(cache => {
            if (!cache) {
                this.documentsCached = [];
                return;
            }

            const documents = this.documents || [];

            documents.forEach(doc => {
                let uri = this.utilityService.getDocumentUri(doc);
                let promise = cache.match(uri);
                promise.then(cached => {
                    documentsCachedTemp[doc.id] = !!cached;
                });
                promises.push(promise);
            });

            Promise.all(promises).then(() => this.documentsCached = documentsCachedTemp);
        });
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
        this.globalStateService.currentDocumentTitle = document.title;

        if (!this.mobileBrowser) {
            let safeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.documentBaseUrl
                + this.encodeURI(this.currentDocument.urlSafeUrl) + '/' + this.currentDocument.ifeedIdHmac);

            this.documentUrl = safeResourceUrl;
        } else {
            window.location.href = this.getDocumentUrl();
        }

        this.showingDocument = true;
    }

    getSafeDocumentUrl(): SafeUrl {
        return this.documentUrl;
    }

    getDocumentUrlForDocument(document: Document): string {
        return this.documentBaseUrl + this.encodeURI(document.urlSafeUrl) + '/' + document.ifeedIdHmac;
    }

    getDocumentUrl(): string {
        return this.documentBaseUrl + this.encodeURI(this.currentDocument.urlSafeUrl) + '/'
            + this.currentDocument.ifeedIdHmac;
    }

    backToList(): void {
        this.resetIframeUrl();

        // Asynchronous since it mitigates an issue in IE where the screen just turns white for a while
        setTimeout(() => {
            this.router.navigate(['/user/ifeed/' + this.globalStateService.getCurrentIfeedId()]);
        });

        // Possibly we have a new cache entry so we want to reflect that in the list.
        this.updateDocumentsCacheStatus();
    }

    resetIframeUrl() {
        this.documentUrl = this.sanitizer.bypassSecurityTrustResourceUrl("about:blank");
    }

    isCached(document: Document) {
        return this.documentsCached[document.id];
    }
}
