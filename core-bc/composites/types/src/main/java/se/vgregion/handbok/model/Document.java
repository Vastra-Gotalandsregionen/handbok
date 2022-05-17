package se.vgregion.handbok.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Patrik Björk
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document implements Serializable {

    private static final long serialVersionUID = 581572780109029286L;

    private String id;
    private String url;
    private String url_t;
    private String title;
    private String urlSafeUrl;
    @JsonProperty(value = "dc.subject.keywords")
    private String[] dcSubjectKeywords;
    @JsonProperty(value = "dc.subject.authorkeywords")
    private String[] dcSubjectAuthorkeywords;
    @JsonProperty(value = "dc.date.issued")
    private Date dcDateIssued;
    @JsonProperty(value = "core:ArchivalObject.core:CreatedDateTime")
    private Date createdDateTime;
    @JsonProperty(value = "dc.publisher.forunit.flat")
    private String dcPublisherForUnitFlat;

    @JsonProperty(value = "vgr:VgrExtension.vgr:PublishedForUnit")
    private String[] vgrExtensionPublishedForUnit;

    public Document() {
    }

    public Document(String title, String url) {
        this.title = title;
        this.url = url;
    }

    private String ifeedIdHmac;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl_t() {
        return url_t;
    }

    public void setUrl_t(String url_t) {
        this.url_t = url_t;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIfeedIdHmac() {
        return ifeedIdHmac;
    }

    public void setIfeedIdHmac(String ifeedIdHmac) {
        this.ifeedIdHmac = ifeedIdHmac;
    }

    public String getUrlSafeUrl() {
        return urlSafeUrl;
    }

    public void setUrlSafeUrl(String urlSafeUrl) {
        this.urlSafeUrl = urlSafeUrl;
    }

    public String[] getDcSubjectKeywords() {
        return dcSubjectKeywords;
    }

    public void setDcSubjectKeywords(String[] dcSubjectKeywords) {
        this.dcSubjectKeywords = dcSubjectKeywords;
    }

    public String[] getDcSubjectAuthorkeywords() {
        return dcSubjectAuthorkeywords;
    }

    public void setDcSubjectAuthorkeywords(String[] dcSubjectAuthorkeywords) {
        this.dcSubjectAuthorkeywords = dcSubjectAuthorkeywords;
    }

    public Date getDcDateIssued() {
        return dcDateIssued;
    }

    public void setDcDateIssued(Date dcDateIssued) {
        this.dcDateIssued = dcDateIssued;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getDcPublisherForUnitFlat() {
        return dcPublisherForUnitFlat;
    }

    public void setDcPublisherForUnitFlat(String dcPublisherForUnitFlat) {
        if (dcPublisherForUnitFlat != null) {
            dcPublisherForUnitFlat = dcPublisherForUnitFlat.split("/")[0];
        }

        this.dcPublisherForUnitFlat = dcPublisherForUnitFlat;
    }

    public String[] getVgrExtensionPublishedForUnit() {
        return vgrExtensionPublishedForUnit;
    }

    public void setVgrExtensionPublishedForUnit(String[] vgrExtensionPublishedForUnit) {
        this.vgrExtensionPublishedForUnit = vgrExtensionPublishedForUnit;
    }
}
