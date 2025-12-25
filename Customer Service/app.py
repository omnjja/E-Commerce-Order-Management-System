from flask import Flask, jsonify, request
import mysql.connector
import requests

app = Flask(__name__)

ORDER_SERVICE_URL = "http://localhost:5001/api/orders"

conn = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = conn.cursor(dictionary=True)

@app.route("/")
def home():
    return "Customer Service is running!"

# Get all customers
@app.route("/api/customers", methods=["GET"])
def get_all_customers():
    cursor.execute("SELECT * FROM customers")
    customers = cursor.fetchall()

    return jsonify(customers)

# Get customer profile
@app.route("/api/customers/<int:customer_id>", methods=["GET"])
def get_customer(customer_id):
    cursor.execute(
        "SELECT * FROM customers WHERE customer_id = %s",
        (customer_id,)
    )
    customer = cursor.fetchone()
    if not customer:
        return jsonify({"error": "Customer not found"}), 404
    return jsonify(customer)

# Get customer order history (calls Order Service)
@app.route("/api/customers/<int:customer_id>/orders", methods=["GET"])
def get_customer_orders(customer_id):
    # Check customer exists first
    cursor.execute(
        "SELECT customer_id FROM customers WHERE customer_id=%s",
        (customer_id,)
    )
    if not cursor.fetchone():
        return jsonify({"error": "Customer not found"}), 404

    resp = requests.get(f"{ORDER_SERVICE_URL}?customer_id={customer_id}")
    if resp.status_code != 200:
        return jsonify({"error": "Order service unavailable"}), 500

    return jsonify(resp.json())

# Update loyalty points
@app.route("/api/customers/<int:customer_id>/loyalty", methods=["PUT"])
def update_loyalty(customer_id):
    data = request.get_json()
    points = data.get("loyalty_points")

    cursor.execute(
        "UPDATE customers SET loyalty_points=%s WHERE customer_id=%s",
        (points, customer_id)
    )
    conn.commit()

    return jsonify({"message": "Loyalty points updated successfully"})
NOTIFICATION_SERVICE_URL = "http://localhost:5005/api/notifications"

@app.route("/api/customers/<int:customer_id>/notifications", methods=["GET"])
def get_customer_notifications(customer_id):
    cursor.execute(
        "SELECT customer_id FROM customers WHERE customer_id=%s",
        (customer_id,)
    )
    if not cursor.fetchone():
        return jsonify({"error": "Customer not found"}), 404

    try:
        #  Notification Service
        resp = requests.get(f"{NOTIFICATION_SERVICE_URL}?customer_id={customer_id}")
        if resp.status_code == 200:
            return jsonify(resp.json())
        else:
            return jsonify({"error": "Notification service unavailable"}), 500
    except Exception as e:
        return jsonify({"error": f"Failed to fetch notifications: {e}"}), 500


if __name__ == "__main__":
    app.run(port=5004, debug=True)