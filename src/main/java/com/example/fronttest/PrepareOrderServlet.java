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

@WebServlet("/prepareOrder")
public class PrepareOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String[] productIds = request.getParameterValues("product_id");

            if (productIds == null || productIds.length == 0) {
                request.setAttribute("error", "Please select at least one product.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

            JSONArray productsArray = new JSONArray();
            HttpClient client = HttpClient.newHttpClient();

            for (String pid : productIds) {
                int productId = Integer.parseInt(pid);
                String qtyParam = request.getParameter("quantity_" + pid);
                int qty = Integer.parseInt(qtyParam);

                // Inventory API call
                HttpRequest invReq = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:5002/api/inventory/check/" + productId))
                        .GET()
                        .build();

                HttpResponse<String> invRes = client.send(invReq, HttpResponse.BodyHandlers.ofString());

                JSONObject inventoryJson = new JSONObject(invRes.body());
                int availableQty = inventoryJson.getInt("quantity_available");

                if (qty > availableQty) {
                    request.setAttribute(
                            "error",
                            "Not enough stock for product " + inventoryJson.getString("product_name") +
                                    ". Available: " + availableQty
                    );
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                    return;
                }

                // add product
                JSONObject prod = new JSONObject();
                prod.put("product_id", productId);
                prod.put("quantity", qty);
                productsArray.put(prod);
            }

            // Pricing Service
            JSONObject pricingPayload = new JSONObject();
            pricingPayload.put("products", productsArray);

            HttpRequest pricingReq = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5003/api/pricing/calculate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(pricingPayload.toString()))
                    .build();

            HttpResponse<String> pricingRes = client.send(pricingReq, HttpResponse.BodyHandlers.ofString());
            JSONObject pricingJson = new JSONObject(pricingRes.body());

            // send to checkout.jsp
            request.setAttribute("products", productsArray);
            request.setAttribute("pricing", pricingJson);
            request.getRequestDispatcher("checkout.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error while preparing order", e);
        }
    }
}

