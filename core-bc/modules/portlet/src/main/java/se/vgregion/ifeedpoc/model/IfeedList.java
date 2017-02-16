package se.vgregion.ifeedpoc.model;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Patrik Björk
 */
@Entity
@Table(name = "vgr_handbok_ifeed_list")
//@Table(name = "vgr_ifeed_poc_ifeed_list", indexes = {@Index(columnList = "name")})
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
//        this.ifeeds.add(new Ifeed(1L, "Avvikelser", "4410369"));
//        this.ifeeds.add(new Ifeed(2L, "Infektion", "4410375"));
//        this.ifeeds.add(new Ifeed(3L, "IT-stöd och Telefoni", "4410963"));
//        this.ifeeds.add(new Ifeed(4L, "Patientsäkerhet", "4411556"));
//        this.ifeeds.add(new Ifeed(5L, "Vårdhygien", "4411565"));
/*
        this.ifeeds.add(new Ifeed("Avvikelser", "http://ifeed.vgregion.se/iFeed-web/documentlists/4410369/metadata.json?by=&dir=asc"));
        this.ifeeds.add(new Ifeed("Infektion", "http://ifeed.vgregion.se/iFeed-web/documentlists/4410375/metadata.json?by=&dir=asc"));
        this.ifeeds.add(new Ifeed("IT-stöd och Telefoni", "http://ifeed.vgregion.se/iFeed-web/documentlists/4410963/metadata.json?by=&dir=asc"));
        this.ifeeds.add(new Ifeed("Patientsäkerhet", "http://ifeed.vgregion.se/iFeed-web/documentlists/4411556/metadata.json?by=&dir=asc"));
        this.ifeeds.add(new Ifeed("Vårdhygien", "http://ifeed.vgregion.se/iFeed-web/documentlists/4411565/metadata.json?by=&dir=asc"));
*/
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
