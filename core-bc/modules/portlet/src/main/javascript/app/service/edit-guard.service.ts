import { Injectable }     from '@angular/core';
import { CanActivate }    from '@angular/router';
import { IfeedService } from "./ifeed.service";

@Injectable()
export class EditGuard implements CanActivate {


    constructor(private ifeedService: IfeedService) {
    }

    canActivate(): boolean {
        console.log('EditGuard#canActivate called');
        return this.ifeedService.hasEditPermission;
    }
}