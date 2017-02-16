import {Ifeed} from "./ifeed.model";
export class IfeedList {
    id: number;
    name: string;
    ifeeds: Ifeed[];
    preferencesUserIds: string[];
}