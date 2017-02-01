import { Injectable } from '@angular/core';
import {Ifeed} from "../view/user/user.component";

@Injectable()
export class IfeedService {

    private currentIfeedId: number = null;
    private currentIfeedName: string = null;
    currentDocumentTitle: string;
    ajaxUrl: string;
    hasPreferencesPermission: boolean;
    ifeeds: [Ifeed] = null;

    setCurrentIfeedId(id: number): void {
        console.log('setCurrentIfeedId: ' + id);

        this.currentIfeedId = id;

        this.updateIfeedName();
    }

    private updateIfeedName() {
        if (this.currentIfeedId === null || this.ifeeds === null) {
            return;
        }

        this.currentIfeedName = null;
        for (let ifeed of this.ifeeds) {
            if (ifeed.id === this.currentIfeedId) {
                this.currentIfeedName = ifeed.name;
            }
        }

        this.currentIfeedId = this.currentIfeedId;
    }

    getCurrentIfeedId(): number {
        return this.currentIfeedId;
    }

    getCurrentIfeedName(): string {
        return this.currentIfeedName;
    }

    setIfeeds(ifeeds: [Ifeed]) {
        this.ifeeds = ifeeds;
        this.updateIfeedName();
    }
}