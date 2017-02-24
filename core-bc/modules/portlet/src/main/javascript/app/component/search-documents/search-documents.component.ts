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

@Component({
    selector: 'search-documents',
    styleUrls: ['./search-documents.component.css'],
    templateUrl: './search-documents.component.html',
    encapsulation: ViewEncapsulation.None
})
export class SearchDocumentsComponent implements OnInit {

    stateCtrl: FormControl;
    searchResults: Observable<QueryResponseEntry[]>; // todo make typed
    selectedEntry: DocumentAndIfeedEntry;// = {id: -1, value: ''};

    constructor(private restService: RestService,
                private ifeedService: IfeedService,
                private errorHandler: ErrorHandler,
                private router: Router) {
        this.stateCtrl = new FormControl();
        this.searchResults = this.stateCtrl.valueChanges
            .startWith(null)
            .mergeMap(query => this.restService.queryIfeedListDocuments(this.ifeedService.bookName, query))
            .map((queryResponse: QueryResponse) => queryResponse.queryResponseEntries);
    }

    ngOnInit(): void {
    }

    onFocus(): any {
        this.selectedEntry = null;
    }

    onClick(): any {
        console.log('onClick: ' + JSON.stringify(this.selectedEntry));
        this.goto(this.selectedEntry);
    }

    toString(object: any): string {
        return JSON.stringify(object);
    }

    onKeyUp(): any {
        console.log('onKeyUp: ' + JSON.stringify(this.selectedEntry));
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
            this.router.navigate(['/user/ifeed/' + entry.ifeed.id], extras);
        }
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