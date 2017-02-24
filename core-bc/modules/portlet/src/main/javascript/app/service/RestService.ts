import {Http, Response, RequestOptions, Headers, URLSearchParams} from "@angular/http";
import {IfeedService} from "./ifeed.service";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {IfeedList} from "../model/ifeed-list.model";
import {AuthHttp} from "angular2-jwt";
import {PortletSelectedIfeedList} from "../model/portlet-selected-ifeed-list.model";
import {QueryResponse} from "../model/query-response-entries.model";

@Injectable()
export class RestService {

    constructor(private http: Http,
                private ifeedService: IfeedService,
                private authHttp: AuthHttp) {
    }

    public getDocumentsForIfeed(ifeedId: string): Observable<Response> {
        return this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + ifeedId + "/document");
    }

    public getAllIfeedLists(): Observable<IfeedList[]> {
        return this.http.get(this.ifeedService.ajaxUrl + "/ifeed").map(response => <IfeedList[]>response.json());
    }

    public getIfeedList(ifeedListName: string): Observable<IfeedList> {
        return this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + ifeedListName).map(response => <IfeedList>response.json());
    }

    public saveIfeedList(ifeedList: IfeedList): Observable<IfeedList> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        return this.authHttp.put(this.ifeedService.ajaxUrl + "/edit/saveIfeedList", JSON.stringify(ifeedList), options)
            .map(response => <IfeedList>response.json());
    }

    public saveAllIfeedLists(ifeedLists: IfeedList[]): Observable<IfeedList[]> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.authHttp.put(this.ifeedService.ajaxUrl + "/edit/saveAllIfeedLists", JSON.stringify(ifeedLists), options)
            .map(response => <IfeedList[]>response.json());
    }

    public saveSelectedIfeedList(portletSelectedIfeedList: PortletSelectedIfeedList): Observable<PortletSelectedIfeedList> {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        return this.authHttp.put(this.ifeedService.ajaxUrl + "/edit/saveSelectedIfeedList", JSON.stringify(portletSelectedIfeedList), options)
            .map(response => <PortletSelectedIfeedList>response.json());
    }

    public queryIfeedListDocuments(ifeedListName: string, query: string): Observable<QueryResponse> {
        if (!query) {
            let queryResponse = new QueryResponse();
            return Observable.of(queryResponse);
        }

        let headers = new Headers({ 'Content-Type': 'application/json' });

        let params: URLSearchParams = new URLSearchParams();
        params.set("query", query);

        let options = new RequestOptions({
            headers: headers,
            search: params
        });

        return this.http.get(this.ifeedService.ajaxUrl + "/ifeedList/" + ifeedListName + "/document", options).map(response => <QueryResponse>response.json());
    }
}