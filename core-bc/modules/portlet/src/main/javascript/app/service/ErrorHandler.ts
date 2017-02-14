import {Injectable} from "@angular/core";
import {ErrorDialogComponent} from "../component/error-dialog/error-dialog.component";
import {MdDialog, MdDialogRef} from "@angular/material";
import {Location} from "@angular/common";

@Injectable()
export class ErrorHandler {

    constructor(private dialog: MdDialog,
                private location: Location) {
    }

    public notifyError(error: any): void {
        let dialogRef: MdDialogRef<ErrorDialogComponent> = this.dialog.open(ErrorDialogComponent);
        dialogRef.componentInstance.err = error;
        dialogRef.afterClosed().subscribe(_ => {
            this.location.go('/');
            location.reload();
        });
    }
}