import {Component, OnInit} from '@angular/core';
import {Http, Response} from "@angular/http";
import {Observable} from 'rxjs';
import 'rxjs/add/operator/map';
import {GlobalStateService} from "../../service/global-state.service";
import {Ifeed} from "../../model/ifeed.model";
import {ErrorHandler} from "../../service/ErrorHandler";
import {Router} from "@angular/router";
import {RestService} from "../../service/RestService";
import {Document} from "../../model/document.model";
import {DomSanitizer} from "@angular/platform-browser";
import {UtilityService} from "../../service/utility.service";
import {Location} from "@angular/common";

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
                private sanitizer: DomSanitizer,
                private restService: RestService,
                private errorHandler: ErrorHandler,
                private router: Router,
                private location: Location,
                private utilityService: UtilityService) {
    }

    ngOnInit(): void {
        this.needsConfiguration = !(this.globalStateService.bookName && this.globalStateService.bookName.length > 0);

        this.globalStateService.setCurrentIfeedId(null);
        this.globalStateService.currentDocumentTitle = null;

        this.hasAdminPermission = this.globalStateService.hasAdminPermission;
        let observableResponse: Observable<Response> = this.http.get(this.globalStateService.ajaxUrl + "/ifeedList/" + this.globalStateService.bookId);
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

    cacheIfeeds() {
        for (const ifeed of <[Ifeed]>this.ifeeds) {
            let subscribeToRequest: Observable<Response> = this.restService.getDocumentsForIfeed(ifeed.id);
            let subscribeToIfeedRequest: Observable<Response> = this.restService.getIfeed(ifeed.id);

            let currentSubscription = Observable.forkJoin(subscribeToRequest, subscribeToIfeedRequest)
                .subscribe(join => {

                        // Check if if we got the response for the current request and that the client hasn't already requested another feed.
                        const documents = <[Document]>join[0].json();
                        const ifeed = <Ifeed>join[1].json();

                        // const ifeedUrl = this.globalStateService.ajaxUrl + 'ifeed/' + ifeed.id; don't need to add to cache since it is already fetched

                        const self = this;
                        caches.keys().then((keys: string[]) => {
                            keys.filter((k: string) => k.indexOf('handbok') > -1).forEach(key => {
                                caches.open(key).then(function (cache) {
                                    const ifeedWebPageUrl = self.location.prepareExternalUrl('/user/ifeed/' + ifeed.id);
                                    cache.add(ifeedWebPageUrl);

                                    documents.forEach(doc => {
                                        console.log('should cache: ' + self.getDocumentUrl(doc));
                                        /*if (!this.utilityService.mobileAndTabletCheck()) {
                                                          let safeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.globalStateService.ajaxUrl + "/document/"
                                                              + this.encodeURI(document.urlSafeUrl) + '/' + document.ifeedIdHmac);

                                                          this.documentUrl = safeResourceUrl;
                                                      } else {*/

                                        cache.add(self.getDocumentUrl(doc));

                                        const documentWebPageUrl = self.location.prepareExternalUrl('/user/ifeed/' + ifeed.id + '?urlSafeUrl=' + doc.urlSafeUrl + '&ifeedIdHmac=' + doc.ifeedIdHmac);//'//self.router.url
                                        cache.add(documentWebPageUrl);
                                        // console.log('added: ' + document.location.href);

                                        // }
                                    });
                                });
                            });
                        });
                        /*
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
                                                    }*/

                    },
                    err => {

//                        this.errorHandler.notifyError(err);

                    }
                );
        }

        return false;
    }

    getDocumentUrl(document: Document): string {
        return this.globalStateService.ajaxUrl + "/document/" + this.encodeURI(document.urlSafeUrl) + '/'
            + document.ifeedIdHmac;
    }

    encodeURI(input: string): string {
        return encodeURIComponent(input);
    }
}
