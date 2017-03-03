import { Injectable }     from '@angular/core';
import { CanActivate, Router }    from '@angular/router';
import { GlobalStateService } from "./global-state.service";

@Injectable()
export class AdminGuard implements CanActivate {


    constructor(private globalStateService: GlobalStateService,
                private router: Router) {
    }

    canActivate(): boolean {
        if (this.globalStateService.hasAdminPermission) {
            return true;
        } else {
            this.router.navigate(['/']);
            return false;
        }
    }
}