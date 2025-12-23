<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.JSONArray, org.json.JSONObject" %>
<html>
<head>
    <title>Checkout</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .card {
            background: white;
            padding: 25px;
            width: 450px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        h2 { text-align: center; }
        table { width: 100%; border-collapse: collapse; margin-top: 15px; }
        th, td { padding: 8px; text-align: center; border-bottom: 1px solid #ddd; }
        .total { font-weight: bold; font-size: 18px; margin-top: 10px; }
        button {
            width: 100%;
            padding: 10px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 15px;
        }
        button:hover { background-color: #218838; }
        .cancel {
            display: block;
            text-align: center;
            margin-top: 10px;
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>

<div class="card">
    <h2>🧾 Order Review</h2>

    <table>
        <tr>
            <th>Product ID</th>
            <th>Quantity</th>
            <th>Unit Price</th>
            <th>Subtotal</th>
        </tr>
        <%
            JSONArray products = (JSONArray) request.getAttribute("products");
            JSONObject pricing = (JSONObject) request.getAttribute("pricing");
            JSONArray breakdown = pricing.getJSONArray("pricing_breakdown");

            for (int i = 0; i < products.length(); i++) {
                JSONObject prod = products.getJSONObject(i);
                JSONObject detail = breakdown.getJSONObject(i);
        %>
        <tr>
            <td><%= prod.getInt("product_id") %></td>
            <td><%= prod.getInt("quantity") %></td>
            <td><%= detail.getDouble("unit_price") %> EGP</td>
            <td><%= detail.getDouble("subtotal") %> EGP</td>
        </tr>
        <% } %>
    </table>

    <p>Total Before Tax: <%= pricing.getDouble("total_before_tax") %> EGP</p>
    <p>Tax: <%= pricing.getDouble("tax_amount") %> EGP</p>
    <p class="total">Final Total: <%= pricing.getDouble("final_total") %> EGP</p>

    <form method="post" action="submitOrder">
        <% for (int i = 0; i < products.length(); i++) {
            JSONObject prod = products.getJSONObject(i);
        %>
        <input type="hidden" name="product_id" value="<%= prod.getInt("product_id") %>">
        <input type="hidden" name="quantity" value="<%= prod.getInt("quantity") %>">
        <% } %>

        Customer ID:
        <input type="number" name="customer_id" value="<%= session.getAttribute("customer_id") %>" required style="width:100%;padding:6px;" disabled>

        <button type="submit">✅ Confirm Order</button>
    </form>

    <a href="index.jsp" class="cancel">❌ Cancel</a>
</div>

</body>
</html>

