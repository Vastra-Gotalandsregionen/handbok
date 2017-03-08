import {Component, OnInit, OnDestroy} from "@angular/core";
import {RequestOptions, Headers} from "@angular/http";
import "rxjs/add/operator/map";
import {GlobalStateService} from "../../service/global-state.service";
import {IfeedList} from "../../model/ifeed-list.model";
import {Ifeed} from "../../model/ifeed.model";
import {DragulaService} from "ng2-dragula";
import {ErrorHandler} from "../../service/ErrorHandler";
import {AuthHttp} from "angular2-jwt";

@Component({
    templateUrl: './admin.component.html',
    styleUrls: [
        './admin.component.css',
        '../../../../../../node_modules/dragula/dist/dragula.min.css'
    ]
})
export class AdminComponent implements OnInit, OnDestroy {

    ifeedList: IfeedList = null;
    bookName: string = null;
    saveButtonText: string = "Spara";
    saveButtonClass: string = "btn-primary";
    movingEntry: Ifeed;
    previousIndex: number;

    constructor(private http: AuthHttp,
                private globalStateService: GlobalStateService,
                private dragulaService: DragulaService,
                private errorHandler: ErrorHandler) {

        dragulaService.setOptions('ifeed-rows', {
            moves: function (el: any, container: any, handle: any) {
                return handle.className .indexOf('handle') > -1;
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

    ngOnInit(): void {

        this.http.get(this.globalStateService.ajaxUrl + "/ifeed/" + this.globalStateService.bookId)
            .map(response => response.json())
            .subscribe(
                json => {
                    this.ifeedList = <IfeedList> json;
                },
                err => {
                    this.errorHandler.notifyError(err);
                }
            );

        this.bookName = this.globalStateService.bookName;
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
        this.http.put(this.globalStateService.ajaxUrl + "/ifeed/", JSON.stringify(this.ifeedList), options)
            .map(response => response.json())
            .subscribe(
                json => {
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
                    this.errorHandler.notifyError(err);
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