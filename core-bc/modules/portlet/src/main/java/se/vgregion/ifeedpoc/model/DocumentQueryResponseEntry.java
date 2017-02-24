package se.vgregion.ifeedpoc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrik Björk
 */
public class DocumentQueryResponseEntry {

    private Ifeed ifeed;
    private List<Document> documents = new ArrayList<>();

    public DocumentQueryResponseEntry(Ifeed ifeed, List<Document> documents) {
        this.ifeed = ifeed;
        this.documents = documents;
    }

    public Ifeed getIfeed() {
        return ifeed;
    }

    public void setIfeed(Ifeed ifeed) {
        this.ifeed = ifeed;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
}
