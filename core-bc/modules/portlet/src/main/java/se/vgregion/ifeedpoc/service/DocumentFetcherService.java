package se.vgregion.ifeedpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.vgregion.ifeedpoc.model.Document;

import java.io.IOException;
import java.net.URL;

/**
 * @author Patrik Björk
 */
@Service
public class DocumentFetcherService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Cacheable
    public Document[] fetchDocuments(String feedId) throws IOException {

        String url = String.format("http://ifeed.vgregion.se/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc", feedId);

        return JSON_MAPPER.readValue(new URL(url), Document[].class);
    }
}
