package com.example.fronttest;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// http://localhost:8080/FrontTest_war_exploded/viewOrdersHistory?customer_id=3

@WebServlet("/viewOrdersHistory")
public class ViewOrdersHistoryServlet extends HttpServlet {

    private static final String CUSTOMER_SERVICE_URL = "http://localhost:5004/api/customers";
    private static final String ORDER_SERVICE_URL = "http://localhost:5001/api/orders";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

//        String customerIdParam = request.getParameter("customer_id");
//        int customerId = 0;
//        if (customerIdParam != null) {
//            try {
//                customerId = Integer.parseInt(customerIdParam);
//            } catch (NumberFormatException e) {
//                throw new ServletException("Invalid customer_id parameter");
//            }
//        } else {
//            throw new ServletException("customer_id parameter is required");
//        }
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        int customerId = (Integer) session.getAttribute("customer_id");


        try {
            HttpClient client = HttpClient.newHttpClient();

            // get all orders to customer id
            HttpRequest customerOrdersReq = HttpRequest.newBuilder()
                    .uri(URI.create(CUSTOMER_SERVICE_URL + "/" + customerId + "/orders"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> customerOrdersRes =
                    client.send(customerOrdersReq, HttpResponse.BodyHandlers.ofString());

            if (customerOrdersRes.statusCode() != 200) {
                throw new RuntimeException("Failed to fetch customer orders: " + customerOrdersRes.body());
            }

            JSONArray ordersArray = new JSONArray(customerOrdersRes.body());
            JSONArray ordersDetailsArray = new JSONArray();

            // get order details
            for (int i = 0; i < ordersArray.length(); i++) {
                JSONObject order = ordersArray.getJSONObject(i);
                int orderId = order.getInt("order_id");

                HttpRequest orderDetailsReq = HttpRequest.newBuilder()
                        .uri(URI.create(ORDER_SERVICE_URL + "/" + orderId))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> orderDetailsRes =
                        client.send(orderDetailsReq, HttpResponse.BodyHandlers.ofString());

                if (orderDetailsRes.statusCode() == 200) {
                    JSONObject orderDetails = new JSONObject(orderDetailsRes.body());
                    ordersDetailsArray.put(orderDetails);
                } else {
                    System.out.println("Failed to fetch order details for order: " + orderId);
                }
            }

            // forward to viewOrdersHistory JSP
            request.setAttribute("ordersHistory", ordersDetailsArray);
            request.getRequestDispatcher("viewOrdersHistory.jsp").forward(request, response);


        } catch (Exception e) {
            throw new ServletException("Error fetching orders history", e);
        }
    }
}
