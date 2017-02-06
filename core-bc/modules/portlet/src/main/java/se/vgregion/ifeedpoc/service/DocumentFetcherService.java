package se.vgregion.ifeedpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.DocumentResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Patrik Björk
 */
@Service
public class DocumentFetcherService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Cacheable(cacheNames = "default")
    public Document[] fetchDocuments(String feedId) throws IOException {
        URL url = new URL(String.format("http://ifeed.vgregion.se/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc",
                feedId));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);

        InputStream inputStream = urlConnection.getInputStream();

        return JSON_MAPPER.readValue(inputStream, Document[].class);
        /*Document[] documents = new Document[1000];
        for (int i = 0; i < documents.length; i++) {
            Document document = new Document();
            document.setUrl(RandomStringUtils.random(10));
            document.setId("lsakdfj");
            documents[i] = document;
        }

        return documents;*/
    }

    @CachePut(cacheNames = "default")
    public Document[] fetchDocumentsPutCache(String feedId) throws IOException {
        return fetchDocuments(feedId);
    }

    @Cacheable(cacheNames = "default")
    public DocumentResponse fetchDocument(String documentUrl) throws IOException {

        /*String random = RandomStringUtils.random(1000000);

        return new DocumentResponse(random.getBytes(), "text/plan");*/

        URL url = new URL(documentUrl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);

        String contentType = urlConnection.getHeaderField("content-type");

        InputStream inputStream = urlConnection.getInputStream();
//        InputStream inputStream = url.openStream();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IOUtils.copy(inputStream, baos);

        return new DocumentResponse(baos.toByteArray(), contentType);
    }

    @CachePut(cacheNames = "default")
    public DocumentResponse fetchDocumentPutCache(String documentUrl) throws IOException {
        return fetchDocument(documentUrl);
    }
}
