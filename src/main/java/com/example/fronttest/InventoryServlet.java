package com.example.fronttest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.http.*;
import java.net.URI;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * InventoryServlet is responsible for retrieving all available products
 * from the Inventory Service and displaying them in an HTML table.
 * This servlet is typically included inside a JSP page using <jsp:include>.
 */

@WebServlet("/inventoryList")
public class InventoryServlet extends HttpServlet {
    /**
     * Handles GET requests.
     * calls the Inventory Service API and dynamically generates an HTML table containing all products.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        // Create HTTP client to communicate with Inventory Service
        HttpClient client = HttpClient.newHttpClient();
        //fetch all inventory products
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5002/api/inventory/all"))
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            // Convert JSON response to JSONArray
            JSONArray jsonArr = new JSONArray(res.body());
            // build the HTML table
            out.println("<table border='1' cellpadding='10'>");
            out.println("<tr><th>Select</th><th>Name</th><th>Available</th><th>Price</th><th>Quantity</th></tr>");

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                out.println("<tr>");
                out.println("<td><input type='checkbox' name='product_id' value='" + obj.getInt("product_id") + "'></td>");
                out.println("<td>" + obj.getString("product_name") + "</td>");
                out.println("<td>" + obj.getInt("quantity_available") + "</td>");
                out.println("<td>$" + obj.getString("unit_price") + "</td>");
                out.println("<td>");
                out.println("<input type='number' name='quantity_" + obj.getInt("product_id") + "' min='1' max='"
                        + obj.getInt("quantity_available") + "' value='1'>");
                out.println("</td>");
                out.println("</tr>");

            }
            out.println("</table>");

        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        }
    }
}
