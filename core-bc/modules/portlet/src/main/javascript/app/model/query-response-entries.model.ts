import {Ifeed} from "./ifeed.model";
import {Document} from "./document.model";

export class QueryResponse {
    queryResponseEntries: QueryResponseEntry[] = [];
}

export class QueryResponseEntry {
    ifeed: Ifeed;
    documents: Document[] = [];
}