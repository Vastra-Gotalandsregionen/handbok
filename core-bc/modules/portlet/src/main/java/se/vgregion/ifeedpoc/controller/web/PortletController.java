package se.vgregion.ifeedpoc.controller.web;

import com.liferay.portal.util.PortalUtil;
import se.vgregion.ifeedpoc.model.Document;
import se.vgregion.ifeedpoc.model.Ifeed;
import se.vgregion.ifeedpoc.model.IfeedList;
import se.vgregion.ifeedpoc.model.UserDetail;
import se.vgregion.ifeedpoc.model.UserList;
import se.vgregion.ifeedpoc.service.HmacUtil;
import se.vgregion.ifeedpoc.service.IfeedService;
import se.vgregion.ifeedpoc.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.PortletDisplay;
import com.liferay.portal.theme.ThemeDisplay;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import javax.inject.Inject;
import javax.portlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URL;

@Component
@RequestMapping("view")
public class PortletController {

    private static final Logger LOG = LoggerFactory.getLogger(PortletController.class);

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Inject
    private UserService userService;

    @Inject
    private IfeedService ifeedService;

    public PortletController() {
    }

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, ModelMap model) {
        User user = (User) request.getAttribute(WebKeys.USER);
        String userScreenName = user != null ? user.getScreenName() : "anonymous";

        ResourceURL baseResourceUrl = response.createResourceURL();

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        String ajaxUrl = themeDisplay.getPortletDisplay().getURLPortlet();
        ajaxUrl = ajaxUrl.replace("null", "");
        ajaxUrl += "/api";

        model.addAttribute("ajaxURL", ajaxUrl);
        model.addAttribute("standalone", false);
        model.addAttribute("authenticatedUser", userScreenName);
        model.addAttribute("portletId", getPortletId(request));
        model.addAttribute("portletAppContextPath", request.getContextPath() + "/");


        PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

        boolean hasPreferencesPermission = themeDisplay.getPermissionChecker()
                .hasPermission(themeDisplay.getLayout().getGroupId(), portletDisplay.getId(),
                        portletDisplay.getResourcePK(), "PREFERENCES");

        model.addAttribute("hasPreferencesPermission", hasPreferencesPermission);

        return "index";
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

        URL url = new URL(foundIfeed.getFeedId());
        System.out.println("flag1");
//        InputStream inputStream = url.openStream();
        System.out.println("flag2");


        JSON_MAPPER.writeValue(response.getPortletOutputStream(), documentList);
//        IOUtils.copy(inputStream, response.getPortletOutputStream());
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

    @ResourceMapping("userList")
    public void userList(@RequestParam int startIndex, @RequestParam int limit, ResourceResponse response) throws Exception {
        LOG.debug("Got list request for users with startIndex {} and limit {}", startIndex, limit);

        UserList users = this.userService.getPortalUserList(startIndex, limit);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        //Automatic JSON serialization doesn't work yet in Portlet MVC
        //See: https://jira.spring.io/browse/SPR-7344
        JSON_MAPPER.writeValue(response.getPortletOutputStream(), users);
    }

    @ResourceMapping("userDetail")
    public void userDetail(@RequestParam long userId, ResourceResponse response) throws Exception {
        LOG.debug("Got detail request for user with id {}", userId);

        UserDetail userDetail = this.userService.getPortalUserDetail(userId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSON_MAPPER.writeValue(response.getPortletOutputStream(), userDetail);
    }

    private String getPortletId(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
        return portletDisplay.getId();
    }
}
