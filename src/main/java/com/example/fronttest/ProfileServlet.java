package com.example.fronttest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private static final String CUSTOMER_SERVICE_URL = "http://localhost:5004/api/customers";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ======================================
        // 1. Check if customer is selected/logged in
        // ======================================
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int customerId = (Integer) session.getAttribute("customer_id");


        try {
            // ======================================
            // 2. Call Customer Service to get profile data
            // ======================================
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest getReq = HttpRequest.newBuilder()
                    .uri(URI.create(CUSTOMER_SERVICE_URL + "/" + customerId))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> getRes = client.send(getReq, HttpResponse.BodyHandlers.ofString());

            System.out.println("Customer API response: " + getRes.body());

            JSONObject customerJson = null;
            if (getRes.statusCode() == 200) {
                customerJson = new JSONObject(getRes.body());
            }
            // ======================================
            // 3. Forward customer data to profile page
            // ======================================
            request.setAttribute("customer", customerJson);
            request.getRequestDispatcher("profile.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Error fetching customer profile", e);
        }
    }
}
