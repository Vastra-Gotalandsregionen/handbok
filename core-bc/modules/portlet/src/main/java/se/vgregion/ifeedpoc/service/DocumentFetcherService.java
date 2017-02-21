package se.vgregion.ifeedpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.DocumentResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * @author Patrik Björk
 */
@Service
public class DocumentFetcherService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final int TIMEOUT = 30000;

    @Value("${ifeed.documents.url}")
    private String ifeedDocumentsUrl;

    @CacheEvict(cacheNames = "default")
    public void evictCache() {

    }

    @Cacheable(cacheNames = "default")
    public Document[] fetchDocuments(String feedId) throws IOException {
        URL url = new URL(String.format(ifeedDocumentsUrl, feedId));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setConnectTimeout(TIMEOUT);
        urlConnection.setReadTimeout(TIMEOUT);

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

        try {
            URL url = new URL(documentUrl);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);

            String contentType = urlConnection.getHeaderField("content-type");

            InputStream inputStream = urlConnection.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            IOUtils.copy(inputStream, baos);

            return new DocumentResponse(baos.toByteArray(), contentType);
        } catch (SocketTimeoutException e) {
            throw new IOException("Document with URL=" + documentUrl + " couldn't be fetched within reasonable time.");
        }
    }

    @CachePut(cacheNames = "default")
    public DocumentResponse fetchDocumentPutCache(String documentUrl) throws IOException {
        return fetchDocument(documentUrl);
    }
}
