//package com.example.fronttest;
//
//import jakarta.servlet.*;
//import jakarta.servlet.http.*;
//import jakarta.servlet.annotation.WebServlet;
//import java.io.IOException;
//import java.net.http.*;
//import java.net.URI;
//import org.json.JSONObject;
//
//@WebServlet("/submitOrder")
//public class OrderServlet extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        String customerId = request.getParameter("customer_id");
//        String productId = request.getParameter("product_id");
//        String quantity = request.getParameter("quantity");
//
//        String jsonPayload = String.format(
//                "{\"customer_id\":%s,\"products\":[{\"product_id\":%s,\"quantity\":%s}],\"total_amount\":0}",
//                customerId, productId, quantity
//        );
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest flaskRequest = HttpRequest.newBuilder()
//                .uri(URI.create("http://localhost:5001/api/orders/create"))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
//                .build();
//
//        try {
//            HttpResponse<String> flaskResponse =
//                    client.send(flaskRequest, HttpResponse.BodyHandlers.ofString());
//
//            // تنسيق JSON قبل إرساله للـ JSP
//            JSONObject json = new JSONObject(flaskResponse.body());
//            String prettyJson = json.toString(4); // 4 spaces indent
//
//            request.setAttribute("orderResponse", prettyJson);
//            request.getRequestDispatcher("confirmation.jsp").forward(request, response);
//
//        } catch (InterruptedException e) {
//            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }
//}

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // =========================
            // 1️⃣ استلام البيانات من checkout.jsp
            // =========================
            String[] productIds = request.getParameterValues("product_id");
            String[] quantities  = request.getParameterValues("quantity");
            int customerId = Integer.parseInt(request.getParameter("customer_id"));

            if (productIds == null || quantities == null) {
                throw new RuntimeException("No products received");
            }

            // =========================
            // 2️⃣ تكوين productsArray
            // =========================
            JSONArray productsArray = new JSONArray();

            for (int i = 0; i < productIds.length; i++) {
                JSONObject product = new JSONObject();
                product.put("product_id", Integer.parseInt(productIds[i]));
                product.put("quantity", Integer.parseInt(quantities[i]));
                productsArray.put(product);
            }

            // =========================
            // 3️⃣ إعادة حساب الأسعار (Pricing Service)
            // =========================
            JSONObject pricingPayload = new JSONObject();
            pricingPayload.put("products", productsArray);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest pricingReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5003/api/pricing/calculate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(pricingPayload.toString()))
                    .build();

            HttpResponse<String> pricingRes =
                    client.send(pricingReq, HttpResponse.BodyHandlers.ofString());

            JSONObject pricingJson = new JSONObject(pricingRes.body());

            // =========================
            // 4️⃣ إنشاء الطلب (Order Service)
            // =========================
            JSONObject orderPayload = new JSONObject();
            orderPayload.put("customer_id", customerId);
            orderPayload.put("products", productsArray);
            orderPayload.put("total_amount", pricingJson.getDouble("final_total"));

            HttpRequest orderReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5001/api/orders"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(orderPayload.toString()))
                    .build();

            HttpResponse<String> orderRes =
                    client.send(orderReq, HttpResponse.BodyHandlers.ofString());

            // =========================
            // 5️⃣ إرسال البيانات لصفحة confirmation.jsp
            // =========================
            request.setAttribute("products", productsArray);
            request.setAttribute("pricing", pricingJson);
            request.setAttribute("orderResponse", orderRes.body());

            request.getRequestDispatcher("confirmation.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error while submitting order", e);
        }
    }
}


