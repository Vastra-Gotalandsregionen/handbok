package se.vgregion.ifeedpoc.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vgr_handbok_ifeed")
public class Ifeed {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String feedId;

    public Ifeed() {
    }

    public Ifeed(String name, String feedId) {
        this.name = name;
        this.feedId = feedId;
    }

    public Ifeed(Long id, String name, String feedId) {
        this.id = id;
        this.name = name;
        this.feedId = feedId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
}