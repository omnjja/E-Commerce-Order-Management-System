<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.JSONObject" %>

<%
    JSONObject customer = (JSONObject) request.getAttribute("customer");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer Profile</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        h1 { color: #333; }
        .profile-container { max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ccc; border-radius: 8px; margin-top: 100px  }
        p { font-size: 16px; line-height: 1.5;}
        strong { color: #555; }
    </style>
</head>
<body>
<div class="profile-container">
    <h1>Profile Details</h1>
    <%
        if (customer != null) {
    %>
    <p><strong>ID:</strong> <%= customer.getInt("customer_id") %></p>
    <p><strong>Name:</strong> <%= customer.getString("name") %></p>
    <p><strong>Email:</strong> <%= customer.getString("email") %></p>
    <p><strong>Phone:</strong> <%= customer.getString("phone") %></p>
    <p><strong>Loyalty Points:</strong> <%= customer.getInt("loyalty_points") %></p>
    <%
    } else {
    %>
    <p>Customer data not available.</p>
    <%
        }
    %>
</div>
</body>
</html>
