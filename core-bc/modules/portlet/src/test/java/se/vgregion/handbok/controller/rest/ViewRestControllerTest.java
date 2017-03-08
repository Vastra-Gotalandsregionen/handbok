package se.vgregion.handbok.controller.rest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.vgregion.handbok.model.Document;
import se.vgregion.handbok.model.DocumentQueryResponse;
import se.vgregion.handbok.model.DocumentQueryResponseEntry;
import se.vgregion.handbok.model.DocumentResponse;
import se.vgregion.handbok.model.Ifeed;
import se.vgregion.handbok.model.IfeedList;
import se.vgregion.handbok.model.PortletSelectedIfeedList;
import se.vgregion.handbok.repository.IfeedListRepository;
import se.vgregion.handbok.repository.IfeedRepository;
import se.vgregion.handbok.repository.PortletSelectedIfeedListRepository;
import se.vgregion.handbok.service.DocumentFetcherService;
import se.vgregion.handbok.service.HmacUtil;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Patrik Björk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context.xml")
@Rollback
public class ViewRestControllerTest {

    @Autowired
    private IfeedListRepository ifeedListRepository;

    @Autowired
    private IfeedRepository ifeedRepository;

    @Autowired
    private PortletSelectedIfeedListRepository portletSelectedIfeedListRepository;

    private DocumentFetcherService documentFetcherService;

    private ViewRestController viewRestController;
    private Document document1;
    private IfeedList ifeedList1;

    @Before
    public void setup() throws Exception {

        documentFetcherService = mock(DocumentFetcherService.class);

        viewRestController = new ViewRestController(
                ifeedListRepository, ifeedRepository, portletSelectedIfeedListRepository, documentFetcherService);

        portletSelectedIfeedListRepository.deleteAll();
        ifeedListRepository.deleteAll();

        ifeedList1 = new IfeedList();
        ifeedList1.setName("ifeedList1");

        Ifeed ifeed1_1 = new Ifeed();
        ifeed1_1.setId("ifeedId1_1");
        ifeed1_1.setFeedId("ifeedIfeedId1_1");

        Ifeed ifeed1_2 = new Ifeed();
        ifeed1_2.setId("ifeedId1_2");
        ifeed1_2.setFeedId("ifeedIfeedId1_2");

        IfeedList ifeedList2 = new IfeedList();
        ifeedList2.setName("ifeedList2");

        ifeedList1.getIfeeds().add(ifeed1_1);
        ifeedList1.getIfeeds().add(ifeed1_2);

        ifeedListRepository.saveAndFlush(ifeedList1);
        ifeedListRepository.saveAndFlush(ifeedList2);

        // Setup documentFetcherService
        document1 = new Document("ifeed-doc1", "http://url1.com");
        document1.setDcSubjectAuthorkeywords(new String[]{"subjectAuthorkeyword"});
        document1.setDcSubjectKeywords(new String[]{"subjectKeyword"});
        Document document2 = new Document("ifeed-doc2", "http://url2.com");
        Document document3 = new Document("ifeed-doc3", "http://url3.com");
        Document document4 = new Document("ifeed-doc4", "http://url4.com");

        document1.setIfeedIdHmac(HmacUtil.calculateRFC2104HMAC(document1.getUrl()));
        document1.setUrlSafeUrl(Base64.encodeBase64URLSafeString(document1.getUrl().getBytes("UTF-8")));

        when(documentFetcherService.fetchDocuments(eq("ifeedIfeedId1_1"))).thenReturn(new Document[]{document1, document2});
        when(documentFetcherService.fetchDocuments(eq("ifeedIfeedId1_2"))).thenReturn(new Document[]{document3, document4});

        when(documentFetcherService.fetchDocument(eq("http://url1.com")))
                .thenReturn(new DocumentResponse(
                        "theContent".getBytes("UTF-8"),
                        "application/pdf"));
    }

    @Test
    public void getAllIfeedLists() throws Exception {
        List<IfeedList> allIfeedLists = viewRestController.getAllIfeedLists();

        assertEquals(2, allIfeedLists.size());
    }

    @Test
    public void getIfeedList() throws Exception {
        IfeedList ifeedList = ifeedListRepository.findAll().get(0);
        ResponseEntity<IfeedList> ifeedList2 = viewRestController.getIfeedList(ifeedList.getId());

        assertEquals((long) ifeedList.getId(), (long) ifeedList2.getBody().getId());
    }

    @Test
    public void queryDocumentTitles() throws Exception {
        ResponseEntity<DocumentQueryResponse> response = viewRestController.queryDocumentTitles(ifeedList1.getId(), "doc");

        int count = 0;
        for (DocumentQueryResponseEntry responseEntry : response.getBody().getDocumentQueryResponseEntry()) {
            count += responseEntry.getDocuments().size();
        }

        assertEquals(4, count);
    }

    @Test
    public void queryDocumentTitlesSubjectKeywords() throws Exception {
        ResponseEntity<DocumentQueryResponse> response = viewRestController.queryDocumentTitles(ifeedList1.getId(), "ubjectKey");

        int count = 0;
        for (DocumentQueryResponseEntry responseEntry : response.getBody().getDocumentQueryResponseEntry()) {
            count += responseEntry.getDocuments().size();
        }

        assertEquals(1, count);
    }

    @Test
    public void queryDocumentTitlesSubjectAuthorkeywords() throws Exception {
        ResponseEntity<DocumentQueryResponse> response = viewRestController.queryDocumentTitles(ifeedList1.getId(), "subjectAuthorkeyword");

        int count = 0;
        for (DocumentQueryResponseEntry responseEntry : response.getBody().getDocumentQueryResponseEntry()) {
            count += responseEntry.getDocuments().size();
        }

        assertEquals(1, count);
    }

    @Test
    public void getDocuments() throws Exception {
        ResponseEntity<Document[]> responseEntity = viewRestController.getDocuments("ifeedId1_1");

        assertEquals(2, responseEntity.getBody().length);
    }

    @Test
    public void getDocument() throws Exception {
        ResponseEntity<InputStreamResource> document = viewRestController.getDocument(document1.getUrlSafeUrl(), document1.getIfeedIdHmac());

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        IOUtils.copy(document.getBody().getInputStream(), output);
        assertEquals("theContent", new String(output.toString("UTF-8")));
    }

    @Test
    public void saveIfeedList() throws Exception {
        viewRestController.saveIfeedList(new IfeedList());

        assertEquals(3, viewRestController.getAllIfeedLists().size());
    }

    @Test
    public void saveAllIfeedLists() throws Exception {
        List<IfeedList> newIfeedLists = new ArrayList<>();

        IfeedList newIfeedList = new IfeedList();

        newIfeedLists.add(newIfeedList);

        viewRestController.saveAllIfeedLists(newIfeedLists);

        // The old ones should be deleted and the new persisted.
        assertEquals(1, viewRestController.getAllIfeedLists().size());
    }

    @Test
    public void saveSelectedIfeedList() throws Exception {

        PortletSelectedIfeedList portletSelectedIfeedList = new PortletSelectedIfeedList("resourcePk", ifeedList1);
        viewRestController.saveSelectedIfeedList(portletSelectedIfeedList);

        assertEquals(1, portletSelectedIfeedListRepository.findAll().size());
    }

}