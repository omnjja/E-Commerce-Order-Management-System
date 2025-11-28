package com.example.fronttest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;

@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String productId = request.getParameter("product_id");
        if (productId == null) productId = "1";

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5002/api/inventory/check/" + productId))
                .build();

        HttpResponse<String> res = null;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response.getWriter().write("Inventory Service Response: " + res.body());
    }
}
