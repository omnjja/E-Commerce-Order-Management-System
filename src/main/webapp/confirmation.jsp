<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.*" %>
<html>
<head>
    <title>Order Confirmation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            padding: 40px;
        }

        h2 {
            color: #28a745;
            text-align: center;
        }

        table {
            border-collapse: collapse;
            width: 80%;
            max-width: 700px;
            margin: 20px auto;
            background-color: white;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }

        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #007bff;
            color: white;
        }

        .totals td {
            font-weight: bold;
        }

        a.back-btn {
            display: inline-block;
            margin: 20px auto;
            padding: 12px 25px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            text-align: center;
            transition: background-color 0.3s ease;
        }

        a.back-btn:hover {
            background-color: #0056b3;
        }

    </style>
</head>
<body>

<h2 >✅ Order Confirm Successfully</h2>

<%
    String jsonResponse = (String) request.getAttribute("orderResponse");
    if (jsonResponse != null) {
        JSONObject json = new JSONObject(jsonResponse);
        JSONObject order = json.getJSONObject("order");
        JSONObject pricing = json.getJSONObject("pricing_details");

//        out.println("<p style='text-align:center; font-size:16px;'>" + json.getString("message") + "</p>");

        // جدول المنتجات
        out.println("<table>");
        out.println("<tr><th>Product ID</th><th>Quantity</th><th>Unit Price</th><th>Subtotal</th></tr>");
        JSONArray breakdown = pricing.getJSONArray("pricing_breakdown");
        for (int i = 0; i < breakdown.length(); i++) {
            JSONObject item = breakdown.getJSONObject(i);
            out.println("<tr>");
            out.println("<td>" + item.getInt("product_id") + "</td>");
            out.println("<td>" + item.getInt("quantity") + "</td>");
            out.println("<td>$" + item.getDouble("unit_price") + "</td>");
            out.println("<td>$" + item.getDouble("subtotal") + "</td>");
            out.println("</tr>");
        }

        // الصفوف الخاصة بالمجموعات
        out.println("<tr class='totals'><td colspan='3'>Total Before Tax</td><td>$" + pricing.getDouble("total_before_tax") + "</td></tr>");
        out.println("<tr class='totals'><td colspan='3'>Tax</td><td>$" + pricing.getDouble("tax_amount") + "</td></tr>");
        out.println("<tr class='totals'><td colspan='3'>Final Total</td><td>$" + pricing.getDouble("final_total") + "</td></tr>");

        out.println("</table>");

    } else {
        out.println("<p>No order data available.</p>");
    }
%>

<a href="index.jsp" class="back-btn">Back to Catalog</a>

</body>
</html>



