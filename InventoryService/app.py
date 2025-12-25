from flask import Flask, request, jsonify
import mysql.connector

app = Flask(__name__)


conn = mysql.connector.connect(
    host="localhost",
    user="ecommerce_user",
    password="secure_password",
    database="ecommerce_system"
)
cursor = conn.cursor(dictionary=True)

# Test endpoint
@app.route('/')
def home():
    return "Inventory Service is running!"

@app.route('/api/inventory/check/<int:product_id>', methods=['GET'])
def check_inventory(product_id):
    cursor.execute("SELECT * FROM inventory WHERE product_id = %s", (product_id,))
    product = cursor.fetchone()
    if product:
        return jsonify(product)
    else:
        return jsonify({"error": "Product not found"}), 404

# Endpoint to update inventory
@app.route('/api/inventory/update', methods=['PUT'])
def update_inventory():
    data = request.get_json()
    try:
        product_id = data['product_id']
        new_quantity = data.get('quantity_available') or data.get('quantity')


        cursor.execute(
            "UPDATE inventory SET quantity_available=%s, last_updated=CURRENT_TIMESTAMP WHERE product_id=%s",
            (new_quantity, product_id)
        )
        conn.commit()
        return jsonify({"message": f"Product {product_id} updated successfully"})
    except Exception as e:
        return jsonify({"error": str(e)}), 400

# endpoint to show all products
@app.route('/api/inventory/all', methods=['GET'])
def get_all_inventory():
    cursor.execute("SELECT * FROM inventory")
    products = cursor.fetchall()
    return jsonify(products)


if __name__ == '__main__':
    app.run(port=5002, debug=True)