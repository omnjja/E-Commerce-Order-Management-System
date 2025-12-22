from flask import Flask, request, jsonify
import mysql.connector
import requests

app = Flask(__name__)

CUSTOMER_SERVICE_URL = "http://localhost:5004/api/customers"
INVENTORY_SERVICE_URL = "http://localhost:5002/api/inventory"

conn = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = conn.cursor(dictionary=True)

@app.route("/")
def home():
    return "Notification Service is running!"

@app.route("/api/notifications/send", methods=["POST"])
def send_notification():
    data = request.get_json()
    order_id = data["order_id"]
    customer_id = data["customer_id"]

    # Get customer info
    cust_resp = requests.get(f"{CUSTOMER_SERVICE_URL}/{customer_id}")
    customer = cust_resp.json()

    # Simulate inventory check
    inventory_status = "Items available for delivery"

    # Generate message
    message = f"Your order #{order_id} has been confirmed. {inventory_status}"

    # Simulate sending email
    print(f"EMAIL SENT TO: {customer['email']}")
    print(f"Subject: Order #{order_id} Confirmed")
    print(f"Body: {message}")

    # Log to database
    cursor.execute(
        """
        INSERT INTO notification_log 
        (order_id, customer_id, notification_type, message)
        VALUES (%s, %s, %s, %s)
        """,
        (order_id, customer_id, "EMAIL", message)
    )
    conn.commit()

    return jsonify({"message": "Notification sent successfully"})
@app.route("/api/notifications", methods=["GET"])
def get_notifications():
    customer_id = request.args.get("customer_id")
    if not customer_id:
        return jsonify({"error": "customer_id is required"}), 400

    cursor.execute(
        "SELECT * FROM notification_log WHERE customer_id=%s",
        (customer_id,)
    )
    notifications = cursor.fetchall()
    return jsonify(notifications), 200


if __name__ == "__main__":
    app.run(port=5005, debug=True)
