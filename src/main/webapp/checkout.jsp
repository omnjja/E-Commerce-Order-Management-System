<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            padding: 20px;
        }

        h2 {
            color: #007bff;
            text-align: center;
        }

        form {
            background-color: white;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
            width: 300px;
        }

        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }

        input[type="text"] {
            width: 100%;
            padding: 8px 10px;
            margin-top: 5px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }

        button {
            margin-top: 20px;
            width: 100%;
            padding: 10px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>

<h2>🛒 Order Checkout</h2>

<form method="post" action="submitOrder">
    <label for="customer_id">Customer ID:</label>
    <input type="text" name="customer_id" id="customer_id" required />

    <label for="product_id">Product ID:</label>
    <input type="text" name="product_id" id="product_id" required />

    <label for="quantity">Quantity:</label>
    <input type="text" name="quantity" id="quantity" required />

    <button type="submit">Submit Order</button>
</form>

</body>
</html>

