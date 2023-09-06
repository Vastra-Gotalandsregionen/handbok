package se.vgregion.handbok.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.vgregion.handbok.model.Document;
import se.vgregion.handbok.model.DocumentResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Pattern;

/**
 * @author Patrik Björk
 */
@Service
public class DocumentFetcherService {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final int TIMEOUT = 30000;

    @Value("${ifeed.documents.url}")
    private String ifeedDocumentsUrl;

    public DocumentFetcherService() {
    }

    public DocumentFetcherService(String ifeedDocumentsUrl) {
        this.ifeedDocumentsUrl = ifeedDocumentsUrl;
    }

    public void evictCache() {

    }

    public Document[] fetchDocuments(String feedId) throws IOException {
        URL url = new URL(String.format(ifeedDocumentsUrl, feedId));

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setConnectTimeout(TIMEOUT);
        urlConnection.setReadTimeout(TIMEOUT);

        try (InputStream inputStream = urlConnection.getInputStream()) {
            return JSON_MAPPER.readValue(inputStream, Document[].class);
        }
    }

    public Document[] fetchDocumentsPutCache(String feedId) throws IOException {
        return fetchDocuments(feedId);
    }

    private HttpURLConnection open(String url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

        switch (urlConnection.getResponseCode()) {
            case HttpURLConnection.HTTP_MOVED_PERM:
            case HttpURLConnection.HTTP_MOVED_TEMP:
                String location = urlConnection.getHeaderField("Location");
                URL base = new URL(url);
                URL next = new URL(base, location);  // Deal with relative URLs
                String externalForm = next.toExternalForm();
                if (url.contains("?") && !externalForm.contains("?")) {
                    url = externalForm + "?" + url.split(Pattern.quote("?"))[1];
                } else {
                    url = externalForm;
                }
                return open(url);
        }
        return urlConnection;
    }

    public DocumentResponse fetchDocument(String documentUrl) throws IOException {

        try {
            // URL url = new URL(documentUrl);

            HttpURLConnection urlConnection = open(documentUrl);

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

    public DocumentResponse fetchDocumentPutCache(String documentUrl) throws IOException {
        return fetchDocument(documentUrl);
    }
}
