package se.vgregion.handbok.controller.rest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ViewRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewRestController.class);

    @Autowired
    private IfeedListRepository ifeedListRepository;

    @Autowired
    private IfeedRepository ifeedRepository;

    @Autowired
    private PortletSelectedIfeedListRepository portletSelectedIfeedListRepository;

    @Autowired
    private DocumentFetcherService documentFetcherService;

    public ViewRestController() {
    }

    public ViewRestController(IfeedListRepository ifeedListRepository,
                              IfeedRepository ifeedRepository,
                              PortletSelectedIfeedListRepository portletSelectedIfeedListRepository,
                              DocumentFetcherService documentFetcherService) {
        this.ifeedListRepository = ifeedListRepository;
        this.ifeedRepository = ifeedRepository;
        this.portletSelectedIfeedListRepository = portletSelectedIfeedListRepository;
        this.documentFetcherService = documentFetcherService;
    }

    @PostConstruct
    public void init() {
        documentFetcherService.evictCache();
    }

    @RequestMapping(value = "/ifeedList", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IfeedList> getAllIfeedLists() {
        return ifeedListRepository.findAllByOrderById();
    }

    @RequestMapping(value = "/ifeed/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<Ifeed> getIfeed(@PathVariable("id") String ifeedId) {
        Ifeed ifeed = ifeedRepository.findById(ifeedId);
        return ResponseEntity.ok(ifeed);
    }

    @RequestMapping(value = "/ifeedList/{bookId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<IfeedList> getIfeedList(@PathVariable("bookId") Long bookId) {

        try {
            IfeedList ifeedList = ifeedListRepository.findOne(bookId);

            if (ifeedList != null) {
                return ResponseEntity.ok(ifeedList);
            }
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return ResponseEntity.notFound().build();
    }

    @RequestMapping(value = "/ifeedList/{ifeedListId}/document", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<DocumentQueryResponse> queryDocumentTitles(@PathVariable("ifeedListId") Long ifeedListId,
                                                                     @RequestParam("query") String query) {

        IfeedList ifeedList = ifeedListRepository.findOne(ifeedListId);

        if (ifeedList == null) {
            return ResponseEntity.notFound().build();
        }

        List<Ifeed> ifeeds = ifeedList.getIfeeds();

        DocumentQueryResponse documentQueryResponse = new DocumentQueryResponse();
        for (Ifeed ifeed : ifeeds) {
            try {
                List<Document> documents = getDocumentsArray(ifeed.getId());

                List<Document> filteredDocuments = documents.stream()
                        .filter(document -> {
                            if (document.getTitle().toLowerCase().contains(query.toLowerCase())) {
                                return true;
                            } else if (containsStringWhichContains(document.getDcSubjectKeywords(), query)) {
                                return true;
                            } else if (containsStringWhichContains(document.getDcSubjectAuthorkeywords(), query)) {
                                return true;
                            }
                            else {
                                return false;
                            }
                        })
                        .collect(Collectors.toList());

                if (filteredDocuments.size() > 0) {
                    DocumentQueryResponseEntry entry = new DocumentQueryResponseEntry(ifeed, filteredDocuments);
                    documentQueryResponse.getDocumentQueryResponseEntry().add(entry);
                }
            } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
                LOGGER.error(e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        return ResponseEntity.ok(documentQueryResponse);
    }

    private boolean containsStringWhichContains(String[] strings, String toFind) {
        if (strings == null) {
            return false;
        }

        for (String string : strings) {
            if (string.toLowerCase().contains(toFind.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    @RequestMapping(value = "/ifeed/{id}/document", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<List<Document>> getDocuments(@PathVariable("id") String ifeedId) {

        List<Document> documentList;
        try {
            documentList = getDocumentsArray(ifeedId);
        } catch (IOException | SignatureException | NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(documentList);
    }

    private List<Document> getDocumentsArray(@PathVariable("id") String ifeedId)
            throws IOException, SignatureException, NoSuchAlgorithmException, InvalidKeyException {

        if (ifeedId == null || "null".equals(ifeedId)) {
            return new ArrayList<>();
        }

        Ifeed ifeed = ifeedRepository.findById(ifeedId);

        if (ifeed.getFeedId() == null) {
            return new ArrayList<>();
        }

        List<Document> documentList = Arrays.asList(documentFetcherService.fetchDocuments(ifeed.getFeedId()));

        String sort = ifeed.getSort();

        if (sort != null) {
            switch (sort) {
                case "title":
                    documentList.sort(
                            Comparator.comparing(
                                    Function.identity(),
                                    Comparator.nullsLast(Comparator.comparing(Document::getTitle))
                            )
                    );

                    break;
                case "issuedDate":
                    sortByDateIssued(documentList);
                    break;
            }
        }

        for (Document document : documentList) {
            document.setIfeedIdHmac(HmacUtil.calculateRFC2104HMAC(document.getUrl()));
            document.setUrlSafeUrl(Base64.encodeBase64URLSafeString(document.getUrl().getBytes(StandardCharsets.UTF_8)));
        }
        return documentList;
    }

    void sortByDateIssued(List<Document> documentList) {
        Function<Document, Date> getDateIssued = d -> {
            Date dateIssued = d.getDcDateIssued();

            if (dateIssued == null) {
                dateIssued = d.getCreatedDateTime();
            }

            return dateIssued != null ? dateIssued : new Date(0);
        };

        Comparator<Document> comparator = Comparator.comparing(
                Function.identity(),
                Comparator.nullsLast(Comparator.comparing(getDateIssued))
        );

        documentList.sort(comparator.reversed());
    }

    @RequestMapping(value = "/document/{urlSafeUrl}/{urlHmac}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getDocument(@PathVariable("urlSafeUrl") String urlSafeUrl,
                                      @PathVariable("urlHmac") String urlHmac) {

        try {
            String documentUrl = new String(Base64.decodeBase64(urlSafeUrl), "UTF-8");

            if (!HmacUtil.calculateRFC2104HMAC(documentUrl).equals(urlHmac)) {
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).build();
            }

            DocumentResponse documentResponse = documentFetcherService.fetchDocument(documentUrl);

            InputStream inputStream = new ByteArrayInputStream(documentResponse.getBytes());

            HttpHeaders headers = new HttpHeaders();
            String ct = documentResponse.getContentType();
            if (ct == null) {
                ct = "application/pdf";
            }
            headers.setContentType(MediaType.valueOf(ct));

            return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @RequestMapping(value = "/ifeed", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList putIfeedList(@RequestBody IfeedList ifeedList) {
        IfeedList one = ifeedListRepository.findOne(ifeedList.getId());
        one.setIfeeds(new ArrayList<>());
        ifeedListRepository.saveAndFlush(one);

        for (Ifeed ifeed : ifeedList.getIfeeds()) {
            if (ifeed.getId() == null) {
                ifeed.setId(UUID.randomUUID().toString());
            }
        }

        return ifeedListRepository.saveAndFlush(ifeedList);
    }

    @RequestMapping(value = "/edit/saveIfeedList", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList saveIfeedList(@RequestBody IfeedList ifeedList) {
        return ifeedListRepository.saveAndFlush(ifeedList);
    }

    @RequestMapping(value = "/edit/saveAllIfeedLists", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList[] saveAllIfeedLists(@RequestBody List<IfeedList> ifeedLists) {

        List<IfeedList> all = ifeedListRepository.findAll();
        for (IfeedList currentlyPersisted : all) {
            if (!ifeedLists.contains(currentlyPersisted)) {
                ifeedListRepository.delete(currentlyPersisted);
            }
        }

        IfeedList[] toReturn = new IfeedList[ifeedLists.size()];

        int count = 0;
        for (IfeedList list : ifeedLists) {
            toReturn[count++] = ifeedListRepository.saveAndFlush(list);
        }
        return toReturn;
    }

    @RequestMapping(value = "/edit/saveSelectedIfeedList", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PortletSelectedIfeedList saveSelectedIfeedList(@RequestBody PortletSelectedIfeedList portletSelectedIfeedList) {
        return portletSelectedIfeedListRepository.saveAndFlush(portletSelectedIfeedList);
    }
/*
    @RequestMapping(value = "/cache-manifest", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<ByteArrayResource> getCacheManifest() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("text/cache-manifest"));

        return new ResponseEntity<>(
                new ByteArrayResource(cacheHelperService.composeCacheManifest()),
                headers,
                HttpStatus.OK
        );
    }*/
}

