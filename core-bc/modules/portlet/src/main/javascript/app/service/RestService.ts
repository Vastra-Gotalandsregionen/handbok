import {Http, Response} from "@angular/http";
import {IfeedService} from "./ifeed.service";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable()
export class RestService {

    constructor(private http: Http, private ifeedService: IfeedService) {
    }

    public getDocumentsForIfeed(ifeedId: string): Observable<Response> {
        return this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + ifeedId + "/document");
    }
}