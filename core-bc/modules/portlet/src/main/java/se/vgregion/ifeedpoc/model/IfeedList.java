package se.vgregion.ifeedpoc.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
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

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ifeed> ifeeds = new ArrayList<>();

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

    /*public class Ifeed {

        private String name;
        private String url;

        public Ifeed(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }*/
}
