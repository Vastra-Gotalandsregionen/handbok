package se.vgregion.ifeedpoc.controller.rest;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import org.apache.commons.codec.binary.Base64;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.Ifeed;
import se.vgregion.ifeedpoc.model.IfeedList;
import se.vgregion.ifeedpoc.service.DocumentFetcherService;
import se.vgregion.ifeedpoc.service.HmacUtil;
import se.vgregion.ifeedpoc.repository.IfeedListRepository;
import se.vgregion.ifeedpoc.repository.IfeedRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@Transactional
public class SampleRESTFullController {

    @Autowired
    private IfeedListRepository ifeedListRepository;

    @Autowired
    private IfeedRepository ifeedRepository;

    @Autowired
    private DocumentFetcherService documentFetcherService;

    @RequestMapping(value = "/ifeed", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IfeedList> getAllIfeedLists(HttpServletRequest request) throws SystemException {

        PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();

        return ifeedListRepository.findAll();
    }

    @RequestMapping(value = "/ifeed/{name}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<IfeedList> getIfeedList(@PathVariable("name") String name) throws SystemException {

        IfeedList ifeedList = ifeedListRepository.findByName(name);

        if (ifeedList == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ifeedList);
    }

    @RequestMapping(value = "/ifeed/{id}/document", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Document[] getDocuments(@PathVariable("id") String id) throws SystemException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {

        Ifeed ifeed = ifeedRepository.findById(id);

        Document[] documentList = documentFetcherService.fetchDocuments(ifeed.getFeedId());

        for (Document document : documentList) {
            document.setIfeedIdHmac(HmacUtil.calculateRFC2104HMAC(document.getUrl()));
            document.setUrlSafeUrl(Base64.encodeBase64URLSafeString(document.getUrl().getBytes("UTF-8")));
        }

        return documentList;
    }

    @RequestMapping(value = "/document/{urlSafeUrl}/{urlHmac}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity getDocument(@PathVariable("urlSafeUrl") String urlSafeUrl,
                                      @PathVariable("urlHmac") String urlHmac) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {

        String documentUrl = new String(Base64.decodeBase64(urlSafeUrl), "UTF-8");

        if (!HmacUtil.calculateRFC2104HMAC(documentUrl).equals(urlHmac)) {
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).build();
        }

        URL url = new URL(documentUrl);

        System.out.println("flag1");
        InputStream inputStream = url.openStream();
        System.out.println("flag2");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/pdf"));

        ResponseEntity responseEntity = new ResponseEntity(new InputStreamResource(inputStream), headers, HttpStatus.OK);

        System.out.println("flag3");

        return responseEntity;
    }

    @RequestMapping(value = "/ifeed", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList putIfeedList(@RequestBody IfeedList ifeedList) throws SystemException {
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

}

