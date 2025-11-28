<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 11/28/2025
  Time: 8:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<%
    String result = (String) request.getAttribute("orderResponse");
%>

<h2>Order Confirmation</h2>
<p><%= result %></p>

</body>
</html>
