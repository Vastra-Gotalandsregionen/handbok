package se.vgregion.ifeedpoc.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrik Björk
 */
public class DocumentList {

    private List<Document> documents = new ArrayList<>();

    public DocumentList() {

    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

}
