<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.JSONArray, org.json.JSONObject" %>

<%
    JSONArray ordersHistory = (JSONArray) request.getAttribute("ordersHistory");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Orders History</title>
</head>
<body>
<h1>Orders History</h1>

<%
    if (ordersHistory != null && !ordersHistory.isEmpty()) {
        for (int i = 0; i < ordersHistory.length(); i++) {
            JSONObject orderWrapper = ordersHistory.getJSONObject(i); // الرد كامل من Order Service
            JSONObject orderData = orderWrapper.getJSONObject("order"); // هنا البيانات الأساسية للأوردر
            JSONArray items = orderWrapper.getJSONArray("items"); // عناصر الأوردر
%>
<div style="border:1px solid #ccc; padding:10px; margin-bottom:10px;">
    <p><strong>Order ID:</strong> <%= orderData.getInt("order_id") %></p>
    <p><strong>Total Amount:</strong> <%= orderData.getDouble("total_amount") %></p>
    <p><strong>Created At:</strong> <%= orderData.getString("created_at") %></p>
    <p><strong>Items:</strong></p>
    <ul>
        <%
            for (int j = 0; j < items.length(); j++) {
                JSONObject item = items.getJSONObject(j);
        %>
        <li>
            Product ID: <%= item.getInt("product_id") %>,
            Quantity: <%= item.getInt("quantity") %>,
            Unit Price: <%= item.getDouble("unit_price") %>
        </li>
        <%
            }
        %>
    </ul>
</div>
<%
    }
} else {
%>
<p>No orders found.</p>
<%
    }
%>
</body>
</html>
