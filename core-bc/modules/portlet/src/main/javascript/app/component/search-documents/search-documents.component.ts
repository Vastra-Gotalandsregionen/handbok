import {FormControl} from "@angular/forms";
import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {Observable} from "rxjs";
import {RestService} from "../../service/RestService";
import {IfeedService} from "../../service/ifeed.service";
import {QueryResponse, QueryResponseEntry} from "../../model/query-response-entries.model";
import {ErrorHandler} from "../../service/ErrorHandler";
import {Ifeed} from "../../model/ifeed.model";
import {Document} from "../../model/document.model";
import {Router} from "@angular/router";
import {UtilityService} from "../../service/utility.service";

@Component({
    selector: 'search-documents',
    styleUrls: ['./search-documents.component.css'],
    templateUrl: './search-documents.component.html',
    encapsulation: ViewEncapsulation.None
})
export class SearchDocumentsComponent implements OnInit {

    stateCtrl: FormControl;
    searchResults: Observable<QueryResponseEntry[]>; // todo make typed
    selectedEntry: DocumentAndIfeedEntry;
    documentBaseUrl: string;
    mobileBrowser: boolean;

    constructor(private restService: RestService,
                private ifeedService: IfeedService,
                private errorHandler: ErrorHandler,
                private utilityService: UtilityService,
                private router: Router) {
        this.stateCtrl = new FormControl();
        this.searchResults = this.stateCtrl.valueChanges
            .startWith(null)
            .mergeMap(query => this.restService.queryIfeedListDocuments(this.ifeedService.bookName, query))
            .map((queryResponse: QueryResponse) => queryResponse.queryResponseEntries);
    }

    ngOnInit(): void {
        this.documentBaseUrl = this.ifeedService.ajaxUrl + "/document/";
        this.mobileBrowser = this.utilityService.mobileAndTabletCheck();
    }

    onFocus(): any {
        this.selectedEntry = null;
    }

    onClick(): any {
        this.goto(this.selectedEntry);
    }

    toString(object: any): string {
        return JSON.stringify(object);
    }

    onKeyUp(): any {
        this.goto(this.selectedEntry);
    }

    private goto(entry: DocumentAndIfeedEntry): void {
        if (typeof entry === 'object') {
            let extras = {
                queryParams: {
                    urlSafeUrl: entry.document.urlSafeUrl,
                    ifeedIdHmac: entry.document.ifeedIdHmac
                }
            };

            if (!this.mobileBrowser) {
                this.router.navigate(['/user/ifeed/' + entry.ifeed.id], extras);

            } else {
                window.location.href = this.getDocumentUrl(entry.document);
            }
        }
    }

    getDocumentUrl(document: Document): string {
        return this.documentBaseUrl + encodeURIComponent(document.urlSafeUrl) + '/' + document.ifeedIdHmac;
    }

    displayFn(value: DocumentAndIfeedEntry): string {
        if (value) {
            return value.document.title;
        } else {
            return "";
        }
    }
}

class DocumentAndIfeedEntry {
    document: Document;
    ifeed: Ifeed;
}