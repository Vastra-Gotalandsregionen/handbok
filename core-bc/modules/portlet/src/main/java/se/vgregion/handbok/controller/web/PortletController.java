package se.vgregion.handbok.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import com.liferay.portletmvc4spring.bind.annotation.ResourceMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import se.vgregion.handbok.model.IfeedList;
import se.vgregion.handbok.model.PortletSelectedIfeedList;
import se.vgregion.handbok.repository.IfeedListRepository;
import se.vgregion.handbok.repository.PortletSelectedIfeedListRepository;
import se.vgregion.handbok.service.JwtUtil;

import javax.portlet.*;
import java.util.List;
import java.util.Optional;

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

        // E.g. /group/guest/handbok
        Layout layout = themeDisplay.getLayout();
        Group scopeGroup = themeDisplay.getScopeGroup();

        String basePath = scopeGroup.getPathFriendlyURL(layout.isPrivateLayout(), themeDisplay)
                + scopeGroup.getFriendlyURL() + layout.getFriendlyURL();

        String angularBase = basePath + "/-/a"; // Careful not to change this in liferay-portlet.xml.
        model.addAttribute("angularBase", angularBase);

        Optional<PortletSelectedIfeedList> selected = portletSelectedIfeedListRepository.findById(resourcePK);

        if (selected.isPresent()) {
            IfeedList ifeedList = selected.get().getIfeedList();
            String bookName = ifeedList.getName();
            Long bookId = ifeedList.getId();
            model.addAttribute("bookName", bookName);
            model.addAttribute("bookId", bookId);

            boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookId);
            model.addAttribute("hasAdminPermission", hasAdminPermission);
            String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(), hasAdminPermission ? "admin" : "guest");
            model.addAttribute("jwtToken", jwtToken);
        }

        return "index";
    }

    private boolean isHasAdminPermission(ThemeDisplay themeDisplay, Long bookId)
            throws SystemException, PortalException {

        List<String> preferencesUserIds = ifeedListRepository.findById(bookId).orElseThrow().getPreferencesUserIds();

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
        PortletSelectedIfeedList selected = portletSelectedIfeedListRepository.findById(resourcePK).orElseThrow();
        Long bookId = selected.getIfeedList().getId();

        boolean hasAdminPermission = isHasAdminPermission(themeDisplay, bookId);

        User user = (User) request.getAttribute(WebKeys.USER);

        String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(),
                hasAdminPermission ? "admin": null);

        response.getPortletOutputStream().write(jwtToken.getBytes("UTF-8"));
    }

}
