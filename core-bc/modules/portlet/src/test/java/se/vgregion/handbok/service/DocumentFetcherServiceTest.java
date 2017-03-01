package se.vgregion.handbok.service;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.vgregion.handbok.model.Document;
import spark.Spark;
import spark.SparkBase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author Patrik Björk
 */
public class DocumentFetcherServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFetcherServiceTest.class);

    @Test
    public void fetchDocuments() throws Exception {

        // Given
        InputStream documentsJson = DocumentFetcherServiceTest.class.getClassLoader().getResourceAsStream("documentsJson");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(documentsJson, baos);

        SparkBase.port(9876);

        Spark.get("/", (req, res) -> baos.toString("UTF-8"));

        // Give Spark time to spin up the server asynchronously.
        Thread.sleep(100);

        DocumentFetcherService service = new DocumentFetcherService("http://localhost:9876");

        try {
            Document[] documents = service.fetchDocuments("hgfjhf");

            for (Document document : documents) {
                LOGGER.info(Arrays.asList(document.getDcSubjectAuthorkeywords()).toString());
                LOGGER.info(Arrays.asList(document.getDcSubjectKeywords()).toString());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            fail("Parsing probably failed.");
        } finally {
            SparkBase.stop();
        }
    }
}