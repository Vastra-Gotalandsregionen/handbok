package se.vgregion.ifeedpoc.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "vgr_handbok_ifeed")
public class Ifeed {

    @Id
    private String id;

    private String name;

    private String feedId;

    @ManyToOne
    private IfeedList ifeedList;

    public Ifeed() {
    }

    public Ifeed(String name, String feedId) {
        this.name = name;
        this.feedId = feedId;
    }

    public Ifeed(String id, String name, String feedId) {
        this.id = id;
        this.name = name;
        this.feedId = feedId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public IfeedList getIfeedList() {
        return ifeedList;
    }

    public void setIfeedList(IfeedList ifeedList) {
        this.ifeedList = ifeedList;
    }
}