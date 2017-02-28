package se.vgregion.handbok.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrik Björk
 */
public class DocumentQueryResponse {

    @JsonProperty("queryResponseEntries")
    private List<DocumentQueryResponseEntry> documentQueryResponseEntry = new ArrayList<>();

    public List<DocumentQueryResponseEntry> getDocumentQueryResponseEntry() {
        return documentQueryResponseEntry;
    }

    public void setDocumentQueryResponseEntry(List<DocumentQueryResponseEntry> documentQueryResponseEntry) {
        this.documentQueryResponseEntry = documentQueryResponseEntry;
    }
}