package se.vgregion.ifeedpoc.controller.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;
import java.io.IOException;

@Component
@RequestMapping("edit")
public class EditController {

    private static final Logger LOG = LoggerFactory.getLogger(EditController.class);

    @RenderMapping
    public String view(RenderRequest request, RenderResponse response, ModelMap model) {
        String bookName = request.getPreferences().getValue("bookName", null);

        model.addAttribute("bookName", bookName);
        return "edit";
    }

    @ActionMapping
    public void setBookName(ActionRequest actionRequest, ActionResponse actionResponse)
            throws ReadOnlyException, IOException, ValidatorException {

        String bookName = actionRequest.getParameter("bookName");

        if (bookName != null) {
            PortletPreferences preferences = actionRequest.getPreferences();
            preferences.setValue("bookName", bookName);
            preferences.store();
        }

    }


}
