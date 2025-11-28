from flask import Flask, request, jsonify
import mysql.connector
import requests

app = Flask(__name__)

# Inventory Service URL
INVENTORY_SERVICE_URL = "http://localhost:5002/api/inventory"

# Database connection
conn = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = conn.cursor(dictionary=True)

@app.route('/')
def home():
    return "Pricing Service is running!"

# Endpoint to calculate order pricing
@app.route('/api/pricing/calculate', methods=['POST'])
def calculate_pricing():
    data = request.get_json()
    try:
        products = data['products']  # [{"product_id": 1, "quantity": 2}, ...]

        pricing_breakdown = []
        total = 0.0

        for item in products:
            product_id = item['product_id']
            quantity = item['quantity']

            # Get product price from Inventory Service
            resp = requests.get(f"{INVENTORY_SERVICE_URL}/check/{product_id}")
            if resp.status_code != 200:
                return jsonify({"error": f"Product {product_id} not found"}), 404
            product_data = resp.json()
            unit_price = float(product_data['unit_price'])

            # Apply discount from pricing_rules table
            cursor.execute(
                "SELECT discount_percentage, min_quantity FROM pricing_rules WHERE product_id=%s",
                (product_id,)
            )
            rule = cursor.fetchone()
            discount = 0.0
            if rule and quantity >= rule['min_quantity']:
                discount = float(rule['discount_percentage'])

            price_before_discount = unit_price * quantity
            price_after_discount = price_before_discount * (1 - discount/100)

            pricing_breakdown.append({
                "product_id": product_id,
                "quantity": quantity,
                "unit_price": unit_price,
                "discount_applied": discount,
                "subtotal": round(price_after_discount, 2)
            })

            total += price_after_discount

        # Calculate tax (example: fixed 10% tax rate)
        tax_rate = 0.1
        tax_amount = total * tax_rate
        final_total = total + tax_amount

        return jsonify({
            "pricing_breakdown": pricing_breakdown,
            "total_before_tax": round(total, 2),
            "tax_amount": round(tax_amount, 2),
            "final_total": round(final_total, 2)
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 400

if __name__ == '__main__':
    app.run(port=5003, debug=True)
