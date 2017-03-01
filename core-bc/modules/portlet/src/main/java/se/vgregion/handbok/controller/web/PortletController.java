package se.vgregion.handbok.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import se.vgregion.handbok.model.PortletSelectedIfeedList;
import se.vgregion.handbok.repository.IfeedListRepository;
import se.vgregion.handbok.repository.PortletSelectedIfeedListRepository;
import se.vgregion.handbok.service.JwtUtil;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.ResourceURL;
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

        PortletSelectedIfeedList selected = portletSelectedIfeedListRepository.findOne(resourcePK);

        if (selected != null) {
            String bookName = selected.getIfeedList().getName();
            model.addAttribute("bookName", bookName);

            boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookName);
            model.addAttribute("hasAdminPermission", hasAdminPermission);
            String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(), hasAdminPermission ? "admin" : "guest");
            model.addAttribute("jwtToken", jwtToken);
        }

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

        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);

        String resourcePK = themeDisplay.getPortletDisplay().getResourcePK();
        PortletSelectedIfeedList selected = portletSelectedIfeedListRepository.findOne(resourcePK);
        String bookName = selected.getIfeedList().getName();

        boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookName);

        User user = (User) request.getAttribute(WebKeys.USER);

        String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(),
                hasAdminPermission ? "admin": null);

        response.getPortletOutputStream().write(jwtToken.getBytes("UTF-8"));
    }

}
