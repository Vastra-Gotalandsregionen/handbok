import {FormControl} from "@angular/forms";
import {Component, OnInit, ViewEncapsulation} from "@angular/core";
import {Observable} from "rxjs";
import {RestService} from "../../service/RestService";
import {GlobalStateService} from "../../service/global-state.service";
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
    searchResults: Observable<QueryResponseEntry[]>;
    selectedEntry: DocumentAndIfeedEntry;
    documentBaseUrl: string;
    mobileBrowser: boolean;
    searchInputFocused: boolean;

    constructor(private restService: RestService,
                private errorHandler: ErrorHandler,
                private utilityService: UtilityService,
                private globalStateService: GlobalStateService,
                private router: Router) {
        this.stateCtrl = new FormControl();
        this.searchResults = this.stateCtrl.valueChanges
            .startWith(null)
            .mergeMap(query => this.restService.queryIfeedListDocuments(this.globalStateService.bookName, query))
            .map((queryResponse: QueryResponse) => queryResponse.queryResponseEntries);
    }

    ngOnInit(): void {
        this.documentBaseUrl = this.globalStateService.ajaxUrl + "/document/";
        this.mobileBrowser = this.utilityService.mobileAndTabletCheck();
    }

    onFocus(): any {
        this.selectedEntry = null;
        this.searchInputFocused = this.globalStateService.searchInputFocused = true;
    }

    onBlur(): void {
        this.searchInputFocused = this.globalStateService.searchInputFocused = false;
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