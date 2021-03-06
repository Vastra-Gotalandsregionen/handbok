import { Injectable }     from '@angular/core';
import { CanActivate }    from '@angular/router';
import { GlobalStateService } from "./global-state.service";

@Injectable()
export class EditGuard implements CanActivate {


    constructor(private globalStateService: GlobalStateService) {
    }

    canActivate(): boolean {
        return this.globalStateService.hasEditPermission;
    }
}