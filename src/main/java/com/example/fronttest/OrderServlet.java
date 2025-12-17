package com.example.fronttest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import org.json.JSONObject;

@WebServlet("/submitOrder")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String customerId = request.getParameter("customer_id");
        String productId = request.getParameter("product_id");
        String quantity = request.getParameter("quantity");

        String jsonPayload = String.format(
                "{\"customer_id\":%s,\"products\":[{\"product_id\":%s,\"quantity\":%s}],\"total_amount\":0}",
                customerId, productId, quantity
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest flaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5001/api/orders/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> flaskResponse =
                    client.send(flaskRequest, HttpResponse.BodyHandlers.ofString());

            // تنسيق JSON قبل إرساله للـ JSP
            JSONObject json = new JSONObject(flaskResponse.body());
            String prettyJson = json.toString(4); // 4 spaces indent

            request.setAttribute("orderResponse", prettyJson);
            request.getRequestDispatcher("confirmation.jsp").forward(request, response);

        } catch (InterruptedException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

