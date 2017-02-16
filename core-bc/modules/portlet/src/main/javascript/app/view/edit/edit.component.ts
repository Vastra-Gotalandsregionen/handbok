import {Component, OnInit} from "@angular/core";
import {RestService} from "../../service/RestService";
import {IfeedService} from "../../service/ifeed.service";
import {IfeedList} from "../../model/ifeed-list.model";
import {ErrorHandler} from "../../service/ErrorHandler";
import {Observable} from "rxjs";
import {PortletSelectedIfeedList} from "../../model/portlet-selected-ifeed-list.model";

@Component({
    selector: 'edit-root',
    templateUrl: './edit.component.html',
    styleUrls: ['./edit.component.css']
})
export class EditComponent implements OnInit {
    ifeedList: IfeedList;
    selectedIfeedListId: number;
    userIds: Array<any>;
    saveButtonClass: string = "btn-primary";
    saveButtonText: string = "Spara";
    ifeedLists: IfeedList[];

    constructor(private restService: RestService,
                private ifeedService: IfeedService,
                private errorHandler: ErrorHandler) {

    }

    ngOnInit(): void {
        this.loadIfeedLists();

        if (this.ifeedService.bookName) {
            this.restService.getIfeedList(this.ifeedService.bookName).subscribe((ifeedList: IfeedList) => {
                this.ifeedList = ifeedList;
                setTimeout(() => {
                    // For some reason this must be made async. Otherwise the select component isn't updated with the correct set value.
                    this.selectedIfeedListId = this.ifeedList.id;
                }, 100);

                // Make a local object mapping index to the object {userId: userId}. This is made to be able to iterate over
                // and edit via ngModel.
                let count: number = 0;
                let userIds: Array<any> = [];
                // Make list which can be iterated and edited
                for (let userId of ifeedList.preferencesUserIds) {
                    userIds[count++] = {userId: userId};
                }

                this.userIds = userIds;
            }, error => {
                this.errorHandler.notifyError(error);
            });
        }
    }

    loadIfeedLists() {
        this.restService.getAllIfeedLists().subscribe((ifeedLists: IfeedList[]) => {
            this.ifeedLists = ifeedLists;
        }, error => {
            this.errorHandler.notifyError(error);
        });
    }

    addUserId(): void {
        this.userIds.push({userId: ""});
    }

    deleteUser(entry: any) {
        let index = this.userIds.indexOf(entry);
        this.userIds.splice(index, 1);
    }

    addIfeedList(): void {
        this.ifeedLists.push(new IfeedList());
    }

    deleteIfeedList(ifeedList: IfeedList): void {
        let index = this.ifeedLists.indexOf(ifeedList);
        this.ifeedLists.splice(index, 1);
    }

    changeName(newName: string) {
        console.log(this.ifeedList.name + " -> " + newName);
        // this.restService.changeIfeedListName(this.ifeedList.id, newName);
    }

    getPortletResourcePk(): string {
        return this.ifeedService.portletResourcePk;
    }

    saveAllIfeedLists(): Observable<IfeedList[]> {
        let saveIfeedLists = this.restService.saveAllIfeedLists(this.ifeedLists);

        return saveIfeedLists;
    }

    saveSelectedIfeedList(): Observable<PortletSelectedIfeedList> {
        let selectedIfeedList = this.getSelectedIfeedList(this.selectedIfeedListId);
        let portletSelectedIfeedList = new PortletSelectedIfeedList(this.getPortletResourcePk(), selectedIfeedList);
        this.ifeedService.bookName = selectedIfeedList.name;
        return this.restService.saveSelectedIfeedList(portletSelectedIfeedList);
    }

    getSelectedIfeedList(selectedIfeedListId: number) {
        for (let ifeedList of this.ifeedLists) {
            if (ifeedList.id === selectedIfeedListId) {
                return ifeedList;
            }
        }

        throw Error("Couldn't find selected IfeedList with id=" + selectedIfeedListId);
    }

    save(): void {
        console.log(JSON.stringify(this.userIds));

        this.saveButtonText = "Sparar...";
        this.saveButtonClass = "btn-primary";

        this.ifeedList.preferencesUserIds = [];
        for (let entry of this.userIds) {
            this.ifeedList.preferencesUserIds.push(entry.userId)
        }
        this.restService.saveIfeedList(this.ifeedList)
            .subscribe(
                ifeedList => {
                    this.ifeedList = ifeedList;
                    this.saveButtonText = "Sparat!";
                    this.saveButtonClass = "btn-success";

                    setTimeout(() => {
                        this.saveButtonText = "Spara";
                        this.saveButtonClass = "btn-primary transition";
                        setTimeout(() => {
                            this.saveButtonClass = "btn-primary";
                        }, 2000);
                    }, 3000);
                },
                err => {
                    this.errorHandler.notifyError(err);
                    this.saveButtonText = "Misslyckades";
                    this.saveButtonClass = "btn-danger";
                }
            );
    }

}