import {Component, Optional} from "@angular/core";
import {MdDialogRef} from "@angular/material";

@Component({
    styleUrls: ['./error-dialog.component.css'],
    templateUrl: './error-dialog.component.html'
})
export class ErrorDialogComponent {
    err: any;
    showDetails: boolean = false;
    public dialogRef: MdDialogRef<ErrorDialogComponent>;

    constructor(@Optional() dialogRef: MdDialogRef<ErrorDialogComponent>) {
        this.dialogRef = dialogRef;
    }

    public toggleDetails(): void {
        this.showDetails = !this.showDetails;
    }

    public close(): void {
        this.dialogRef.close();
    }
}