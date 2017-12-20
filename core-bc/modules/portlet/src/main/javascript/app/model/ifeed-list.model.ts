import {Ifeed} from "./ifeed.model";
export class IfeedList {
    id: number;
    name: string;
    area: string;
    ifeeds: Ifeed[];
    preferencesUserIds: string[];
    sort: string;
}