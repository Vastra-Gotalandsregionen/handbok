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
import {CacheService} from "../../service/cache.service";

@Component({
    templateUrl: './user.component.html',
    styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

    hasAdminPermission: boolean;
    ifeeds: [Ifeed];
    needsConfiguration: boolean;
    cacheUpdateInProgress: boolean;

    constructor(private http: Http,
                private globalStateService: GlobalStateService,
                private sanitizer: DomSanitizer,
                private restService: RestService,
                private cacheService: CacheService,
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
        this.cacheUpdateInProgress = true;
        const promiseFullIfeeds = <Promise<any>[]>[];

        Observable.of(...this.ifeeds).flatMap(ifeed => {
            let subscribeToRequest$: Observable<Response> = this.restService.getDocumentsForIfeed(ifeed.id);
            let subscribeToIfeedRequest$: Observable<Response> = this.restService.getIfeed(ifeed.id);
            let cache$ = this.cacheService.getCache();

            return Observable.forkJoin(subscribeToRequest$, subscribeToIfeedRequest$, cache$);
        }).do(join => {

            const documents = <[Document]>join[0].json();
            const ifeed = <Ifeed>join[1].json();
            const cache = <Cache>join[2];

            // this.cacheService.getCache().do(cache => {
            const ifeedWebPageUrl = this.location.prepareExternalUrl('/user/ifeed/' + ifeed.id);

            const promises = <Promise<void>[]>[];
            promises.push(cache.add(ifeedWebPageUrl));

            documents.forEach(doc => {
                /*if (!this.utilityService.mobileAndTabletCheck()) {
                                  let safeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.globalStateService.ajaxUrl + "/document/"
                                      + this.encodeURI(document.urlSafeUrl) + '/' + document.ifeedIdHmac);

                                  this.documentUrl = safeResourceUrl;
                              } else {*/

                promises.push(cache.add(this.utilityService.getDocumentUri(doc)));

                const documentWebPageUrl = this.location.prepareExternalUrl('/user/ifeed/' + ifeed.id + '?urlSafeUrl=' + doc.urlSafeUrl + '&ifeedIdHmac=' + doc.ifeedIdHmac);//'//self.router.url
                promises.push(cache.add(documentWebPageUrl));
            });

            let promiseFullIfeed = Promise.all(promises).then(() => {
                this.cacheService.notifyCacheStatusUpdate(ifeed.id);
            });

            promiseFullIfeeds.push(promiseFullIfeed);
        }, err => {

//                        this.errorHandler.notifyError(err);

        }).toArray().subscribe(x => {
            Promise.all(promiseFullIfeeds).then(() => this.cacheUpdateInProgress = false);
        });

        return false;
    }


}
