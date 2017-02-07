import {Injectable} from "@angular/core";
import {Modal} from "angular2-modal/plugins/bootstrap";

@Injectable()
export class ErrorHandler {

    constructor(/*vcRef: ViewContainerRef,*/ public modal: Modal) {
        // modal.defaultViewContainer = vcRef;
    }

    public notifyError(error: any): void {
        if (error.status) {
            if (error.status >= 300) {
                this.modal.alert()
                    .size('lg')
                    .showClose(true)
                    .title('Oj då, ett fel uppstod')
                    .body(`
            Vilket fel blev det? 
           `)
                    .open();
            }
        }
    }

    public test(): void {
        this.modal.alert()
        // .open().then()
            .size('lg')
            .showClose(true)
            .title('A simple Alert style modal window')
            .body(`
            <h4>Alert is a classic (title/body/footer) 1 button modal window that 
            does not block.</h4>
            <b>Configuration:</b>
            <ul>
                <li>Non blocking (click anywhere outside to dismiss)</li>
                <li>Size large</li>
                <li>Dismissed with default keyboard key (ESC)</li>
                <li>Close wth button click</li>
                <li>HTML content</li>
            </ul>`)
            .open();

    }
}