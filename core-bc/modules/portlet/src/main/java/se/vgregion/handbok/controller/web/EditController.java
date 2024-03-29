package se.vgregion.handbok.controller.web;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portletmvc4spring.bind.annotation.ActionMapping;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import se.vgregion.handbok.model.IfeedList;
import se.vgregion.handbok.model.PortletSelectedIfeedList;
import se.vgregion.handbok.repository.IfeedListRepository;
import se.vgregion.handbok.repository.PortletSelectedIfeedListRepository;
import se.vgregion.handbok.service.JwtUtil;

import javax.portlet.*;
import java.io.IOException;
import java.util.Optional;

@Component
@RequestMapping("edit")
public class EditController {

    private static final Logger LOG = LoggerFactory.getLogger(EditController.class);

    @Autowired
    private IfeedListRepository ifeedListRepository;
    @Autowired
    private PortletSelectedIfeedListRepository portletSelectedIfeedListRepository;

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, ModelMap model) {
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

        String jwtToken = JwtUtil.createToken(user == null ? null : user.getUserId(), "edit");
        model.addAttribute("jwtToken", jwtToken);

        Optional<PortletSelectedIfeedList> selected = portletSelectedIfeedListRepository.findById(resourcePK);

        if (selected.isPresent()) {
            IfeedList ifeedList = selected.get().getIfeedList();
            String bookName = ifeedList.getName();
            model.addAttribute("bookName", bookName);
            model.addAttribute("bookId", ifeedList.getId());
        }

        model.addAttribute("editMode", true);

        return "edit";
    }

    @ActionMapping
    @Transactional
    public void setBookName(ActionRequest actionRequest, ActionResponse actionResponse)
            throws ReadOnlyException, IOException, ValidatorException {

        String bookName = actionRequest.getParameter("bookName");

        if (bookName != null) {
            PortletPreferences preferences = actionRequest.getPreferences();
            preferences.setValue("bookName", bookName);
            preferences.store();

            if (ifeedListRepository.findByName(bookName) == null) {
                IfeedList ifeedList = new IfeedList();
                ifeedList.setName(bookName);
                ifeedListRepository.saveAndFlush(ifeedList);
            }
        }
    }

    private String getPortletId(PortletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();
        return portletDisplay.getId();
    }
}
