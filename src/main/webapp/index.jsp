<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Product Catalog</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f4f6f8; padding: 20px; }
        h2 { text-align: center; }
        .top-links {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { padding: 10px; text-align: center; }
        th { background-color: #007bff; color: white; }
        tr:nth-child(even) { background-color: #f2f2f2; }
        button {
            padding: 8px 15px;
            border: none;
            background-color: #28a745;
            color: white;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover { background-color: #218838; }
    </style>
</head>
<body>

<h2>🛒 Product Catalog</h2>

<div class="top-links">
    <a href="profile.jsp">👤 Profile</a>
    <a href="orders.jsp">📦 Orders History</a>
</div>

<form method="post" action="prepareOrder">

    <!-- الجدول بييجي من الـ Servlet -->
    <jsp:include page="inventoryList"/>

    <div style="text-align:center; margin-top:20px;">
        <button type="submit">🧾 Make Order</button>
    </div>


</body>
</html>



