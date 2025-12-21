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

@WebServlet("/inventoryList")
public class InventoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5002/api/inventory/all"))
                .GET()
                .build();

        try {
            HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
            JSONArray jsonArr = new JSONArray(res.body());

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
