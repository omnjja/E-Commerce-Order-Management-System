<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.json.JSONArray, org.json.JSONObject" %>
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
        h2 { color: #28a745; text-align: center; }
        table { border-collapse: collapse; width: 80%; max-width: 700px; margin: 20px auto; background-color: white; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        th, td { padding: 12px; border: 1px solid #ddd; text-align: center; }
        th { background-color: #007bff; color: white; }
        .totals td { font-weight: bold; }
        a.back-btn { display: inline-block; margin: 20px auto; padding: 12px 25px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; text-align: center; }
        a.back-btn:hover { background-color: #0056b3; }
    </style>
</head>
<body>

<h2>✅ Order Placed Successfully</h2>

<%
    JSONArray products = (JSONArray) request.getAttribute("products");
    JSONObject pricing = (JSONObject) request.getAttribute("pricing");
    JSONArray breakdown = pricing.getJSONArray("pricing_breakdown");

    out.println("<table>");
    out.println("<tr><th>Product ID</th><th>Quantity</th><th>Unit Price</th><th>Subtotal</th></tr>");

    for (int i = 0; i < products.length(); i++) {
        JSONObject prod = products.getJSONObject(i);
        JSONObject detail = breakdown.getJSONObject(i);
        out.println("<tr>");
        out.println("<td>" + prod.getInt("product_id") + "</td>");
        out.println("<td>" + prod.getInt("quantity") + "</td>");
        out.println("<td>" + detail.getDouble("unit_price") + " EGP</td>");
        out.println("<td>" + detail.getDouble("subtotal") + " EGP</td>");
        out.println("</tr>");
    }

    // إجماليات الطلب
    out.println("<tr class='totals'><td colspan='3'>Total Before Tax</td><td>" + pricing.getDouble("total_before_tax") + " EGP</td></tr>");
    out.println("<tr class='totals'><td colspan='3'>Tax</td><td>" + pricing.getDouble("tax_amount") + " EGP</td></tr>");
    out.println("<tr class='totals'><td colspan='3'>Final Total</td><td>" + pricing.getDouble("final_total") + " EGP</td></tr>");
    out.println("</table>");
%>

<a href="index.jsp" class="back-btn">Back to Catalog</a>

</body>
</html>
