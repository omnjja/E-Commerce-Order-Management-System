<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login Simulation</title>
    <style>
        .customer-card {
            max-width: 350px;
            margin: 0 auto 25px auto;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
            text-align: center;
        }

        .customer-card h3 {
            margin-bottom: 15px;
            color: #333;
        }

        .customer-form select {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .customer-form button {
            width: 100%;
            padding: 10px;
            background-color: #007bff;
            color: white;
            font-size: 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .customer-form button:hover {
            background-color: #0056b3;
        }

    </style>
</head>
<body>
<div class="customer-card">
    <h3>👤 Select Customer</h3>

    <form action="selectCustomer" method="post" class="customer-form">
        <select name="customer_id" required>
            <option value="" disabled selected>-- Choose Customer --</option>
            <option value="1">Customer 1</option>
            <option value="2">Customer 2</option>
            <option value="3">Customer 3</option>
        </select>

        <button type="submit">Continue</button>
    </form>
</div>
</body>
</html>
