package se.vgregion.handbok.model;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrik Björk
 */
@Entity
@Table(name = "vgr_handbok_ifeed_list")
public class IfeedList {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OrderColumn
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ifeed> ifeeds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "vgr_handbok_ifeed_list_preferences_user_ids")
    private List<String> preferencesUserIds = new ArrayList<>();

    public IfeedList() {
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

    public List<Ifeed> getIfeeds() {
        return ifeeds;
    }

    public void setIfeeds(List<Ifeed> ifeeds) {
        this.ifeeds = ifeeds;
    }

    public List<String> getPreferencesUserIds() {
        return preferencesUserIds;
    }

    public void setPreferencesUserIds(List<String> preferencesUserIds) {
        this.preferencesUserIds = preferencesUserIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IfeedList ifeedList = (IfeedList) o;

        return id.equals(ifeedList.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
