import {Component, Input, ElementRef, OnInit, Inject} from '@angular/core';
import {Http, Response, RequestOptions, Headers} from "@angular/http";
import {Observable}     from 'rxjs';
import {Router} from "@angular/router";
import 'rxjs/add/operator/map';
import {IfeedService} from "../../service/ifeed.service";
import {IfeedList} from "../../model/ifeed-list.model";
import {Ifeed} from "../../model/ifeed.model";

@Component({
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

    private ifeedList: IfeedList = null;
    private bookName: string = null;
    private saveButtonText: string = "Spara";
    private saveButtonClass: string = "btn-primary";

    constructor(private http: Http, private ifeedService: IfeedService) {
    }

    post(): void {
        let subscribeToRequest: Observable<Response> = this.http.post(this.ifeedService.ajaxUrl + "/ifeed...", "?data={'key':'value'}");

        let currentSubscription = subscribeToRequest
            .map(response => response.json())
            .subscribe(
                json => {

                    console.log(json);


                },
                err => {
                    console.log(err)
                }
            );
    }

    ngOnInit(): void {

        this.http.get(this.ifeedService.ajaxUrl + "/ifeed/" + this.ifeedService.bookName)
            .map(response => response.json())
            .subscribe(
                json => {
                    console.log("AdminComponent: " + JSON.stringify(json));
                    this.ifeedList = <IfeedList> json;
                },
                err => {
                    console.log(err)
                }
            );

        this.bookName = this.ifeedService.bookName;
    }

    addIfeed(): void {
        this.ifeedList.ifeeds.push(new Ifeed());
    }

    deleteIfeed(ifeed: Ifeed): void {
        let index = this.ifeedList.ifeeds.indexOf(ifeed);
        this.ifeedList.ifeeds.splice(index, 1);
    }

    save(): void {
        console.log(JSON.stringify(this.ifeedList));

        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });

        this.saveButtonText = "Sparar...";
        this.saveButtonClass = "btn-primary";
        this.http.put(this.ifeedService.ajaxUrl + "/ifeed/", JSON.stringify(this.ifeedList), options)
            .map(response => response.json())
            .subscribe(
                json => {
                    console.log("AdminComponent: " + JSON.stringify(json));
                    this.ifeedList = <IfeedList> json;
                    this.saveButtonText = "Sparat!";
                    this.saveButtonClass = "btn-success";

                    setTimeout(() => {
                        this.saveButtonText = "Spara";
                        this.saveButtonClass = "btn-primary transition";
                        setTimeout(() => {
                            this.saveButtonClass = "btn-primary";
                        }, 2000);
                    }, 3000);
                },
                err => {
                    console.log(err);
                    this.saveButtonText = "Misslyckades";
                    this.saveButtonClass = "btn-danger";
                }
            );
    }


}