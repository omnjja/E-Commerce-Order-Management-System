from flask import Flask
app = Flask(__name__)

@app.route('/')
def home():
    return "Service running!"

if __name__ == '__main__':
    app.run(port=5004, debug=True)
