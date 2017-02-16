package se.vgregion.ifeedpoc.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.Ifeed;
import se.vgregion.ifeedpoc.model.IfeedList;
import se.vgregion.ifeedpoc.repository.IfeedListRepository;
import se.vgregion.ifeedpoc.repository.PortletSelectedIfeedListRepository;
import se.vgregion.ifeedpoc.service.HmacUtil;
import se.vgregion.ifeedpoc.service.JwtUtil;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Component
@RequestMapping("view")
public class PortletController {

    private static final Logger LOG = LoggerFactory.getLogger(PortletController.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Autowired
    private IfeedListRepository ifeedListRepository;

    @Autowired
    private PortletSelectedIfeedListRepository portletSelectedIfeedListRepository;

    public PortletController() {
    }

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, ModelMap model)
            throws SystemException, PortalException {

        User user = (User) request.getAttribute(WebKeys.USER);
        String userScreenName = user != null ? user.getScreenName() : "anonymous";

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        String urlPortlet = themeDisplay.getPortletDisplay().getURLPortlet(); // E.g. /handbok-portletnull
        String ajaxUrl = urlPortlet.replace("null", "") + "/api";

        ResourceURL resourceUrl = response.createResourceURL();
        String resourcePK = themeDisplay.getPortletDisplay().getResourcePK();

        model.addAttribute("resourceUrl", resourceUrl.toString());
        model.addAttribute("ajaxURL", ajaxUrl);
        model.addAttribute("authenticatedUser", userScreenName);
        model.addAttribute("portletResourcePk", resourcePK);
        model.addAttribute("portletAppContextPath", request.getContextPath() + "/");

        String bookName = portletSelectedIfeedListRepository.findOne(resourcePK).getIfeedList().getName();

        model.addAttribute("bookName", bookName);

        model.addAttribute("bookName", bookName);

        boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookName);

        model.addAttribute("hasAdminPermission", hasAdminPermission);

        String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(), hasAdminPermission ? "admin" : "guest");
        model.addAttribute("jwtToken", jwtToken);

        return "index";
    }

    private boolean isHasAdminPermission(ThemeDisplay themeDisplay, String bookName)
            throws SystemException, PortalException {

        List<String> preferencesUserIds = ifeedListRepository.findByName(bookName).getPreferencesUserIds();

        boolean hasPreferencesPermission = false;
        if (preferencesUserIds.contains(themeDisplay.getUser().getScreenName())) {
            hasPreferencesPermission = true;
        }

        return hasPreferencesPermission;
    }

    @ResourceMapping("refreshToken")
    public void refreshToken(ResourceRequest request, ResourceResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String bookName = request.getPreferences().getValue("bookName", null);

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookName);

        User user = (User) request.getAttribute(WebKeys.USER);

        String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(),
                hasAdminPermission ? "admin": null);

        response.getPortletOutputStream().write(jwtToken.getBytes("UTF-8"));
    }

    @ResourceMapping("getIfeed")
    public void getIfeed(@RequestParam("name") String name, ResourceResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        IfeedList ifeedList = new IfeedList();

        Ifeed foundIfeed = null;

        for (Ifeed ifeed : ifeedList.getIfeeds()) {
            if (ifeed.getName().equals(name)) {
                foundIfeed = ifeed;
            }
        }

        Document[] documentList = JSON_MAPPER.readValue(new URL(foundIfeed.getFeedId()), Document[].class);

        for (Document document : documentList) {
            document.setIfeedIdHmac(HmacUtil.calculateRFC2104HMAC(document.getUrl()));
        }

        System.out.println("flag1");
        System.out.println("flag2");


        JSON_MAPPER.writeValue(response.getPortletOutputStream(), documentList);
        System.out.println("flag3");
    }

    @ResourceMapping("getDocument")
    public void getDocument(@RequestParam("documentUrl") String documentUrl,
                            @RequestParam(value = "ifeedIdHmac", required = false) String ifeedIdHmac,
                            ResourceResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setCharacterEncoding("UTF-8");

        if (!HmacUtil.calculateRFC2104HMAC(documentUrl).equals(ifeedIdHmac)) {
            HttpServletResponse httpServletResponse = PortalUtil.getHttpServletResponse(response);

            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ogiltigt anrop.");

            return;
        }

        URL url = new URL(documentUrl);

        System.out.println("flag1");
        InputStream inputStream = url.openStream();
        System.out.println("flag2");

        IOUtils.copy(inputStream, response.getPortletOutputStream());
        System.out.println("flag3");
    }

    @ResourceMapping("getIfeeds")
    public void getIfeeds(ResourceResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSON_MAPPER.writeValue(response.getPortletOutputStream(), new IfeedList());
    }

    @ResourceMapping("putIfeed")
    public void putIfeed(@RequestParam("data") String data, ResourceResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSON_MAPPER.writeValue(response.getPortletOutputStream(), new IfeedList());
    }

    private String getPortletId(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
        return portletDisplay.getId();
    }
}
