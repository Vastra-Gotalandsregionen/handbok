import {IfeedList} from "./ifeed-list.model";
export class PortletSelectedIfeedList {

    constructor(portletResourcePk: string, ifeedList: IfeedList) {
        this.portletResourcePk = portletResourcePk;
        this.ifeedList = ifeedList;
    }

    portletResourcePk: string;
    ifeedList: IfeedList;
}