package com.example.fronttest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * This servlet simulates a login mechanism by allowing the user
 * to select a customer ID before accessing the system.
 * The selected customer ID is stored in the HTTP session.
 */

@WebServlet("/selectCustomer")
public class SelectCustomerServlet extends HttpServlet {
    private static final String CUSTOMER_SERVICE_URL = "http://localhost:5004/api/customers";

    /**
     * Handles GET requests.
     * Fetches all customers from Customer Service
     * and forwards them to selectCustomer.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(CUSTOMER_SERVICE_URL))
                    .GET()
                    .build();

            HttpResponse<String> httpResponse =
                    client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            JSONArray customers = new JSONArray(httpResponse.body());

            // Pass customers list to JSP
            request.setAttribute("customers", customers);
            request.getRequestDispatcher("selectCustomer.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            throw new ServletException("Failed to load customers", e);
        }
    }

    /**
     * Handles POST requests.
     * Reads the selected customer ID and stores it in the session.
     * After that, redirects the user to the main page (index.jsp).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int customerId = Integer.parseInt(request.getParameter("customer_id"));

        HttpSession session = request.getSession();
        session.setAttribute("customer_id", customerId);

        response.sendRedirect("index.jsp");
    }
}

