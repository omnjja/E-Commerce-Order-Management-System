from flask import Flask, request, jsonify
import datetime
import requests
import mysql.connector

app = Flask(__name__)

# -------- Database Connection --------
def get_db():
    return mysql.connector.connect(
        host="localhost",
        user="ecommerce_user",
        password="secure_password",
        database="ecommerce_system"
    )

# -------- Other Services --------
INVENTORY_SERVICE_URL = "http://localhost:5002/api/inventory"
PRICING_SERVICE_URL = "http://localhost:5003/api/pricing/calculate"


@app.route('/')
def home():
    return "Order Service is running!"


# -------- Create Order --------
@app.route('/api/orders/create', methods=['POST'])
def create_order():
    data = request.get_json()

    customer_id = data.get('customer_id')
    products = data.get('products')

    if not customer_id or not products:
        return jsonify({"error": "Missing parameters"}), 400

    # Check inventory
    for item in products:
        resp = requests.get(f"{INVENTORY_SERVICE_URL}/check/{item['product_id']}")
        if resp.status_code != 200:
            return jsonify({"error": f"Product {item['product_id']} not found"}), 404

        if item['quantity'] > resp.json()['quantity_available']:
            return jsonify({"error": "Insufficient stock"}), 400

    # Pricing Service
    pricing_resp = requests.post(PRICING_SERVICE_URL, json={"products": products})
    if pricing_resp.status_code != 200:
        return jsonify({"error": "Pricing failed"}), 500

    pricing_data = pricing_resp.json()
    total_amount = pricing_data['final_total']

    # Save Order in DB
    db = get_db()
    cursor = db.cursor()

    cursor.execute(
        "INSERT INTO orders (customer_id, total_amount) VALUES (%s, %s)",
        (customer_id, total_amount)
    )
    order_id = cursor.lastrowid

    # Save Order Items
    pricing_items = pricing_data['pricing_breakdown']

    for item in pricing_items:
        cursor.execute(
            """
            INSERT INTO order_items (order_id, product_id, quantity, unit_price)
            VALUES (%s, %s, %s, %s)
            """,
            (
                order_id,
                item['product_id'],
                item['quantity'],
                item['unit_price']
            )
        )

    db.commit()
    cursor.close()
    db.close()

    # Update Inventory
    for item in products:
        product_id = item['product_id']
        ordered_quantity = item['quantity']

        # 1. Get current inventory
        product_resp = requests.get(f"{INVENTORY_SERVICE_URL}/check/{product_id}")
        product_data = product_resp.json()

        current_quantity = product_data['quantity_available']
        new_quantity = current_quantity - ordered_quantity

        # 2. Send correct update payload
        requests.put(
            f"{INVENTORY_SERVICE_URL}/update",
            json={
                "product_id": product_id,
                "quantity_available": new_quantity
            }
        )

    return jsonify({
        "message": "Order created successfully",
        "order_id": order_id,
        "total_amount": total_amount
    }), 201



# -------- Get Order --------
@app.route('/api/orders/<int:order_id>', methods=['GET'])
def get_order(order_id):
    db = get_db()
    cursor = db.cursor(dictionary=True)

    cursor.execute("SELECT * FROM orders WHERE order_id = %s", (order_id,))
    order = cursor.fetchone()

    if not order:
        return jsonify({"error": "Order not found"}), 404

    cursor.execute(
        "SELECT product_id, quantity, unit_price FROM order_items WHERE order_id = %s",
        (order_id,)
    )
    items = cursor.fetchall()

    cursor.close()
    db.close()

    return jsonify({
        "order": order,
        "items": items
    })

@app.route('/api/orders', methods=['GET'])
def get_orders_by_customer():
    customer_id = request.args.get('customer_id')

    if not customer_id:
        return jsonify({"error": "customer_id is required"}), 400

    db = get_db()
    cursor = db.cursor(dictionary=True)

    cursor.execute(
        "SELECT * FROM orders WHERE customer_id = %s",
        (customer_id,)
    )

    orders = cursor.fetchall()

    cursor.close()
    db.close()

    return jsonify(orders), 200

if __name__ == '__main__':
    app.run(port=5001, debug=True)