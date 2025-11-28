from flask import Flask, request, jsonify
import datetime
import requests

app = Flask(__name__)

# In-memory storage for orders
orders = []

# URLs for other services
INVENTORY_SERVICE_URL = "http://localhost:5002/api/inventory"
PRICING_SERVICE_URL = "http://localhost:5003/api/pricing/calculate"

@app.route('/')
def home():
    return "Order Service is running!"

@app.route('/api/orders/create', methods=['POST'])
def create_order():
    data = request.get_json()
    try:
        customer_id = data['customer_id']
        products = data['products']
        total_amount = data.get('total_amount', 0)  # optional, can be calculated by Pricing Service

        if not customer_id or not products:
            return jsonify({"error": "Missing parameters"}), 400

        # --- 1. Check inventory for each product ---
        for item in products:
            product_id = item['product_id']
            quantity = item['quantity']

            resp = requests.get(f"{INVENTORY_SERVICE_URL}/check/{product_id}")
            if resp.status_code != 200:
                return jsonify({"error": f"Product {product_id} not found"}), 404

            product_data = resp.json()
            if quantity > product_data['quantity_available']:
                return jsonify({"error": f"Insufficient stock for product {product_id}"}), 400

        # --- 2. Calculate pricing by calling Pricing Service ---
        pricing_resp = requests.post(PRICING_SERVICE_URL, json={"products": products})
        if pricing_resp.status_code != 200:
            return jsonify({"error": "Pricing calculation failed"}), 500
        pricing_data = pricing_resp.json()
        final_total = pricing_data['final_total']

        # --- 3. Generate order ID and timestamp ---
        order_id = len(orders) + 1
        timestamp = datetime.datetime.now().isoformat()

        # --- 4. Save order ---
        order = {
            "order_id": order_id,
            "customer_id": customer_id,
            "products": products,
            "total_amount": final_total,
            "timestamp": timestamp
        }
        orders.append(order)

        # --- 5. Update inventory after order ---
        for item in products:
            product_id = item['product_id']
            quantity = item['quantity']

            product_resp = requests.get(f"{INVENTORY_SERVICE_URL}/check/{product_id}")
            current_quantity = product_resp.json()['quantity_available']
            new_quantity = current_quantity - quantity

            requests.put(f"{INVENTORY_SERVICE_URL}/update", json={
                "product_id": product_id,
                "quantity_available": new_quantity
            })

        return jsonify({
            "message": "Order created successfully",
            "order": order,
            "pricing_details": pricing_data
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 400

@app.route('/api/orders/<int:order_id>', methods=['GET'])
def get_order(order_id):
    for order in orders:
        if order['order_id'] == order_id:
            return jsonify(order)
    return jsonify({"error": "Order not found"}), 404

if __name__ == '__main__':
    app.run(port=5001, debug=True)
