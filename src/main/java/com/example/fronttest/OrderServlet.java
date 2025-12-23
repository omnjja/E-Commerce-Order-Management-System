package com.example.fronttest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/submitOrder")
public class OrderServlet extends HttpServlet {

    private static final String ORDER_SERVICE_URL = "http://localhost:5001/api/orders/create";
    private static final String PRICING_SERVICE_URL = "http://localhost:5003/api/pricing/calculate";
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:5004/api/customers";
    private static final String NOTIFICATION_SERVICE_URL = "http://localhost:5005/api/notifications/send";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // get data from checkout.jsp
            String[] productIds = request.getParameterValues("product_id");
            String[] quantities = request.getParameterValues("quantity");
            int customerId = Integer.parseInt(request.getParameter("customer_id"));

            if (productIds == null || quantities == null || productIds.length != quantities.length) {
                throw new RuntimeException("No products received or mismatch in quantities");
            }

            // initialize productsArray
            JSONArray productsArray = new JSONArray();
            for (int i = 0; i < productIds.length; i++) {
                JSONObject product = new JSONObject();
                product.put("product_id", Integer.parseInt(productIds[i]));
                product.put("quantity", Integer.parseInt(quantities[i]));
                productsArray.put(product);
            }

            HttpClient client = HttpClient.newHttpClient();

            // Pricing Service
            JSONObject pricingPayload = new JSONObject();
            pricingPayload.put("products", productsArray);

            HttpRequest pricingReq = HttpRequest.newBuilder()
                    .uri(URI.create(PRICING_SERVICE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(pricingPayload.toString()))
                    .build();

            HttpResponse<String> pricingRes = client.send(pricingReq, HttpResponse.BodyHandlers.ofString());
            if (pricingRes.statusCode() != 200) {
                throw new RuntimeException("Pricing service failed: " + pricingRes.body());
            }
            JSONObject pricingJson = new JSONObject(pricingRes.body());

            // Order Service
            JSONObject orderPayload = new JSONObject();
            orderPayload.put("customer_id", customerId);
            orderPayload.put("products", productsArray);

            HttpRequest orderReq = HttpRequest.newBuilder()
                    .uri(URI.create(ORDER_SERVICE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(orderPayload.toString()))
                    .build();

            HttpResponse<String> orderRes = client.send(orderReq, HttpResponse.BodyHandlers.ofString());
            if (orderRes.statusCode() != 201) {
                throw new RuntimeException("Order creation failed: " + orderRes.body());
            }

            JSONObject orderResponseJson = new JSONObject(orderRes.body());
            int orderId = orderResponseJson.getInt("order_id");

            // Update Loyalty Points (Customer Service)
            try {
                HttpRequest getReq = HttpRequest.newBuilder()
                        .uri(URI.create(CUSTOMER_SERVICE_URL + "/" + customerId))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> getRes = client.send(getReq, HttpResponse.BodyHandlers.ofString());

                if (getRes.statusCode() != 200) {
                    System.out.println("Failed to fetch customer data: " + getRes.body());
                } else {
                    JSONObject customerJson = new JSONObject(getRes.body());
                    int currentPoints = customerJson.optInt("loyalty_points", 0);

                    JSONObject loyaltyPayload = new JSONObject();
                    loyaltyPayload.put("loyalty_points", currentPoints + 10);

                    HttpRequest loyaltyReq = HttpRequest.newBuilder()
                            .uri(URI.create(CUSTOMER_SERVICE_URL + "/" + customerId + "/loyalty"))
                            .header("Content-Type", "application/json")
                            .PUT(HttpRequest.BodyPublishers.ofString(loyaltyPayload.toString()))
                            .build();

                    HttpResponse<String> loyaltyRes = client.send(loyaltyReq, HttpResponse.BodyHandlers.ofString());
                    System.out.println("Loyalty update response: " + loyaltyRes.body());
                }
            } catch (Exception e) {
                System.out.println("Failed to update loyalty points: " + e.getMessage());
            }

            // Send Notification (Notification Service)
            JSONObject notifPayload = new JSONObject();
            notifPayload.put("order_id", orderId);
            notifPayload.put("customer_id", customerId);

            try {
                HttpRequest notifReq = HttpRequest.newBuilder()
                        .uri(URI.create(NOTIFICATION_SERVICE_URL))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(notifPayload.toString()))
                        .build();
                HttpResponse<String> notifRes = client.send(notifReq, HttpResponse.BodyHandlers.ofString());
                System.out.println("Notification response: " + notifRes.body());
            } catch (Exception e) {
                System.out.println("Failed to send notification: " + e.getMessage());
            }

            // send data to confirmation.jsp
            request.setAttribute("products", productsArray);
            request.setAttribute("pricing", pricingJson);
            request.setAttribute("orderResponse", orderRes.body());

            request.getRequestDispatcher("confirmation.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error while submitting order", e);
        }
    }
}

