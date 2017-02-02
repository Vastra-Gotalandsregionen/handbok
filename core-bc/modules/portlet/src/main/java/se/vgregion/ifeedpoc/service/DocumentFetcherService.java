package se.vgregion.ifeedpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.vgregion.ifeedpoc.model.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Patrik Björk
 */
@Service
public class DocumentFetcherService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Cacheable(cacheNames = "default")
    public Document[] fetchDocuments(String feedId) throws IOException {

        String url = String.format("http://ifeed.vgregion.se/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc", feedId);

        return JSON_MAPPER.readValue(new URL(url), Document[].class);
    }

    @Cacheable(cacheNames = "default")
    public byte[] fetchDocument(String documentUrl) throws IOException {

        URL url = new URL(documentUrl);

        InputStream inputStream = url.openStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IOUtils.copy(inputStream, baos);

        return baos.toByteArray();
    }
}
