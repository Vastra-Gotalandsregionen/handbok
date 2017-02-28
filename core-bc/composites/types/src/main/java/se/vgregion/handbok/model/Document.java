package se.vgregion.handbok.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

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
}
