<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>

<h3>Ange ämneshandbok för denna portlet-instans</h3>

<portlet:actionURL var="saveBookNameAction">

</portlet:actionURL>

<form method="post" action="<%= saveBookNameAction %>">
    <div class="">
        <input type="text" name="bookName" value="${bookName}"/>
    </div>
    <div>
        <input type="submit" class="btn btn-primary" value="Spara"/>
    </div>
</form>