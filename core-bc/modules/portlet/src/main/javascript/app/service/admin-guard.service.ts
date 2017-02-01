import { Injectable }     from '@angular/core';
import { CanActivate }    from '@angular/router';
import {IfeedService} from "./ifeed.service";

@Injectable()
export class AdminGuard implements CanActivate {


    constructor(private ifeedService: IfeedService) {
    }

    canActivate(): boolean {
        console.log('AdminGuard#canActivate called');
        return this.ifeedService.hasPreferencesPermission;
    }
}