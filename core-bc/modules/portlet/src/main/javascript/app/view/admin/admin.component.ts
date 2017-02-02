import {Component, Input, ElementRef, OnInit, Inject, OnDestroy} from '@angular/core';
import {Http, Response, RequestOptions, Headers} from "@angular/http";
import {Observable}     from 'rxjs';
import 'rxjs/add/operator/map';
import {IfeedService} from "../../service/ifeed.service";
import {IfeedList} from "../../model/ifeed-list.model";
import {Ifeed} from "../../model/ifeed.model";
import {DragulaService} from "ng2-dragula";

@Component({
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.css', '../../../../../../node_modules/dragula/dist/dragula.min.css']
})
export class AdminComponent implements OnInit, OnDestroy {

    private ifeedList: IfeedList = null;
    private bookName: string = null;
    private saveButtonText: string = "Spara";
    private saveButtonClass: string = "btn-primary";
    private movingEntry: Ifeed;
    private previousIndex: number;

    constructor(private http: Http, private ifeedService: IfeedService, private dragulaService: DragulaService) {
        dragulaService.setOptions('ifeed-rows', {
            moves: function (el: any, container: any, handle: any) {
                return handle.className === 'handle';
            }
        });

        dragulaService.drag.subscribe((value: any) => {
            this.onDrag(value.slice(1));
        });

        dragulaService.drop.subscribe((value: any) => {
            this.onDrop(value.slice(1));
        });

        dragulaService.over.subscribe((value: any) => {
            this.onOver(value.slice(1));
        });

        dragulaService.out.subscribe((value: any) => {
            this.onOut(value.slice(1));
        });
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

    ngOnDestroy() {
        this.dragulaService.destroy('ifeed-rows');
    }

    addIfeed(): void {
        this.ifeedList.ifeeds.push(new Ifeed());
    }

    deleteIfeed(ifeed: Ifeed): void {
        let index = this.ifeedList.ifeeds.indexOf(ifeed);
        this.ifeedList.ifeeds.splice(index, 1);
    }

    save(): void {
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

    private onDrag(args: any): void {
        let [e, el] = args;
        let previousIndex = this.previousIndex = e.sectionRowIndex;

        // Save temporarily.
        this.movingEntry = this.ifeedList.ifeeds.slice(previousIndex, previousIndex + 1)[0];

        this.removeClass(e, 'ex-moved');
    }

    private onDrop(args: any) {
        let [e, el] = args;
        let newIndex = e.sectionRowIndex;

        // Remove from previous index.
        this.ifeedList.ifeeds.splice(this.previousIndex, 1);

        // And insert at new index.
        this.ifeedList.ifeeds.splice(newIndex, 0, this.movingEntry);

        this.movingEntry = null;

        this.addClass(e, 'ex-moved');
    }

    private onOver(args: any) {
        let [e, el, container] = args;
        this.addClass(el, 'ex-over');
    }

    private onOut(args: any) {
        let [e, el, container] = args;
        this.removeClass(el, 'ex-over');
    }

    private addClass(el: any, name: string) {
        if (!this.hasClass(el, name)) {
            el.className = el.className ? [el.className, name].join(' ') : name;
        }
    }

    private removeClass(el: any, name: string) {
        if (this.hasClass(el, name)) {
            el.className = el.className.replace(new RegExp('(?:^|\\s+)' + name + '(?:\\s+|$)', 'g'), '');
        }
    }

    private hasClass(el: any, name: string) {
        return new RegExp('(?:^|\\s+)' + name + '(?:\\s+|$)').test(el.className);
    }

}