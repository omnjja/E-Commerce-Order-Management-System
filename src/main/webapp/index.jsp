<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="jakarta.servlet.RequestDispatcher" %>

<html>
<head>
    <title>Product Catalog</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; }
        h2 { text-align: center; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 10px; text-align: center; }
        th { background-color: #007bff; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        button { padding: 5px 10px; border: none; background-color: #28a745; color: white; border-radius: 3px; cursor: pointer; }
        button:hover { background-color: #218838; }
    </style>
</head>
<body>

<h2>🛒 Product Catalog</h2>

<div style="display:flex; justify-content:center;">
    <jsp:include page="inventoryList"/>
</div>


</body>
</html>


