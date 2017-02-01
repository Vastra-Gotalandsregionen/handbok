package se.vgregion.ifeedpoc.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.Ifeed;
import se.vgregion.ifeedpoc.model.IfeedList;
import se.vgregion.ifeedpoc.service.HmacUtil;
import se.vgregion.ifeedpoc.service.IfeedListRepository;
import se.vgregion.ifeedpoc.service.IfeedRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Controller
@Transactional
public class SampleRESTFullController {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Autowired
    private IfeedListRepository ifeedListRepository;

    @Autowired
    private IfeedRepository ifeedRepository;

    @RequestMapping(value = "/ifeed", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList helloSample(HttpServletRequest request) throws SystemException {

        PermissionChecker permissionChecker = PermissionThreadLocal.getPermissionChecker();

        return new IfeedList();
    }

    @RequestMapping(value = "/ifeed/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public IfeedList getIfeedList(@PathVariable("id") Long id) throws SystemException {

//        IfeedList ifeedList = new IfeedList();
//        ifeedList.getIfeeds().add(new Ifeed("theName", "theUrl://example.com"));
//        return ifeedList;
        return ifeedListRepository.findOne(id);
    }

    @RequestMapping(value = "/ifeed/{id}/document", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Document[] getDocuments(@PathVariable("id") Long id) throws SystemException, IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {

//        IfeedList ifeedList = new IfeedList();
//        ifeedList.getIfeeds().add(new Ifeed("theName", "theUrl://example.com"));
//        return ifeedList;
        Ifeed match = null;
        for (Ifeed ifeed : new IfeedList().getIfeeds()) {
            if (ifeed.getId().equals(id)) {
                match = ifeed;
            }
        }

//        String feedId = ifeedRepository.findOne(id).getFeedId();

        String url = String.format("http://ifeed.vgregion.se/iFeed-web/documentlists/%s/metadata.json?by=&dir=asc", match.getFeedId());

        Document[] documentList = JSON_MAPPER.readValue(new URL(url), Document[].class);

        for (Document document : documentList) {
            document.setIfeedIdHmac(HmacUtil.calculateRFC2104HMAC(document.getUrl()));
            document.setUrlSafeUrl(Base64.encodeBase64URLSafeString(document.getUrl().getBytes("UTF-8")));
//            document.setUrlSafeUrl(Base64.getUrlEncoder().encodeToString(document.getUrl().getBytes("UTF-8")));
//            document.setUrlSafeUrl(Base64.getUrlEncoder().encodeToString(document.getUrl().getBytes("UTF-8")));
        }

        return documentList;
    }

    @RequestMapping(value = "/document/{urlSafeUrl}/{urlHmac}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity getDocument(@PathVariable("urlSafeUrl") String urlSafeUrl,
                                      @PathVariable("urlHmac") String urlHmac) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, IOException {

//        String documentUrl = new String(Base64.getUrlDecoder().decode(urlSafeUrl), "UTF-8");
        String documentUrl = new String(Base64.decodeBase64(urlSafeUrl), "UTF-8");

        if (!HmacUtil.calculateRFC2104HMAC(documentUrl).equals(urlHmac)) {
//            HttpServletResponse httpServletResponse = PortalUtil.getHttpServletResponse(response);

            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).build();

        }

        URL url = new URL(documentUrl);

        System.out.println("flag1");
        InputStream inputStream = url.openStream();
        System.out.println("flag2");


        System.out.println("flag3");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/pdf"));

        ResponseEntity responseEntity = new ResponseEntity(new InputStreamResource(inputStream), headers, HttpStatus.OK);

        return responseEntity;
    }

    @RequestMapping(value = "/ifeed", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void putIfeedList(@RequestBody IfeedList ifeedList) throws SystemException {
        ifeedListRepository.saveAndFlush(ifeedList);
    }



}

