import { Injectable } from '@angular/core';
import {Ifeed} from "../model/ifeed.model";

@Injectable()
export class GlobalStateService {

    private currentIfeedId: string = null;
    private currentIfeedName: string = null;
    currentDocumentTitle: string;
    ajaxUrl: string;
    hasAdminPermission: boolean;
    ifeeds: [Ifeed] = null;
    bookName: string;
    resourceUrl: string;
    bookId: number;
    hasEditPermission: boolean;
    portletResourcePk: string;
    searchInputFocused: boolean;
    isIE: boolean;

    setCurrentIfeedId(id: string): void {
        this.currentIfeedId = id;

        this.updateIfeedName();
    }

    private updateIfeedName() {
        if (this.currentIfeedId === null || this.ifeeds === null) {
            this.currentIfeedName = null;
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

    getCurrentIfeedId(): string {
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