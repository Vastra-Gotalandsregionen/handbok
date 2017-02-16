<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<app-root edit-mode="${editMode}" ajax-url="${ajaxURL}" book-name="${bookName}" has-admin-permission="${hasAdminPermission}" jwt-token="${jwtToken}" resource-url="${resourceUrl}" portlet-resource-pk="${portletResourcePk}"></app-root>