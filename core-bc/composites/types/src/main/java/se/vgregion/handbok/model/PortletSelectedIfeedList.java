package se.vgregion.handbok.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Patrik Björk
 */
@Entity
@Table(name = "vgr_handbok_portlet_selected_ifeed_list")
public class PortletSelectedIfeedList {

    @Id
    private String portletResourcePk;

    @ManyToOne
    private IfeedList ifeedList;

    public PortletSelectedIfeedList() {
    }

    public PortletSelectedIfeedList(String portletResourcePk, IfeedList ifeedList) {
        this.portletResourcePk = portletResourcePk;
        this.ifeedList = ifeedList;
    }

    public String getPortletResourcePk() {
        return portletResourcePk;
    }

    public void setPortletResourcePk(String portletResourcePk) {
        this.portletResourcePk = portletResourcePk;
    }

    public IfeedList getIfeedList() {
        return ifeedList;
    }

    public void setIfeedList(IfeedList ifeedList) {
        this.ifeedList = ifeedList;
    }
}
