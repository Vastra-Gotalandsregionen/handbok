import {Component, Input, ElementRef, AfterViewInit} from "@angular/core";
import {Observable} from "rxjs";
import {Response} from "@angular/http";
import {ErrorHandler} from "../../service/ErrorHandler";
@Component({
    selector: 'feedback-button',
    styleUrls: ['./feedback-button.component.css'],
    templateUrl: './feedback-button.component.html'
})
export class FeedbackButtonComponent implements AfterViewInit {
    private clazz: string = "btn btn-primary";
    private showOnlyLoadingIndicator: boolean = false;
    @Input("buttonText") buttonText: string;
    @Input("functionObject") functionObject: any;
    @Input("clickFunction") clickFunction: () => Observable<any>;
    @Input("clickFunctionArgs") clickFunctionArgs: any[];
    @Input("callback") callback: () => void;
    private initialInnerHtml: string;

    constructor(private elementRef: ElementRef,
                private errorHandler: ErrorHandler) {
    }

    ngAfterViewInit(): void {
        let nativeElement: any = this.elementRef.nativeElement;
        this.initialInnerHtml = nativeElement.firstElementChild.innerHTML;
    }

    click(): void {
        let nativeElement: any = this.elementRef.nativeElement;

        this.showOnlyLoadingIndicator = true;
        this.clazz = "btn-primary";

        let observable: Observable<Response>= this.clickFunction.apply(this.functionObject, this.clickFunctionArgs);
        observable.subscribe(
            response => {
                this.showOnlyLoadingIndicator = false;
                this.clazz = 'btn-success';

                nativeElement.firstElementChild.innerHTML = `<i class="icon icon-ok"></i>`;

                setTimeout(() => {
                    nativeElement.firstElementChild.innerHTML = this.initialInnerHtml;
                    this.clazz = "btn-primary transition";
                    setTimeout(() => {
                        this.clazz = "btn-primary";
                    }, 2000);
                }, 3000);

                if (this.callback) {
                    this.callback.apply(this.functionObject);
                }
            },
            error => {
                this.showOnlyLoadingIndicator = false;
                this.errorHandler.notifyError(error);
                let confirmHtml = `Misslyckades`;
                nativeElement.firstElementChild.innerHTML = confirmHtml;
                this.clazz = "btn-danger";
                if (this.callback) {
                    this.callback.apply(this.functionObject);
                }
            }
        )
    }

}