<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<base href="${angularBase}/edit">

<app-root edit-mode="${editMode}"
          ajax-url="${ajaxURL}"
          book-name="${bookName}"
          book-id="${bookId}"
          has-admin-permission="${hasAdminPermission}"
          jwt-token="${jwtToken}"
          resource-url="${resourceUrl}"
          portlet-resource-pk="${portletResourcePk}">

</app-root>