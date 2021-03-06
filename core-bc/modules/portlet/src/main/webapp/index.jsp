<%@page contentType="text/html" pageEncoding="UTF-8" %>

<base href="${angularBase}/view">
<style>
    app-root .icon.icon-spinner {
        animation: rotate .8s infinite;
        animation-timing-function: cubic-bezier(0.495, 0.405, 0.495, 0.635);
        display: inline-block;
    }

    @keyframes rotate {
        from {
            transform: rotate(0deg);
        }
        to {
            transform: rotate(360deg);
        }
    }
</style>

<app-root ajax-url="${ajaxURL}"
          book-name="${bookName}"
          book-id="${bookId}"
          has-admin-permission="${hasAdminPermission}"
          jwt-token="${jwtToken}"
          resource-url="${resourceUrl}">
    Läser in... <span class="loading-indicator"><i class="icon icon-spinner"></i></span>
</app-root>

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">