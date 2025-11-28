package com.example.fronttest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;

@WebServlet("/notify")
public class NotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5005/"))
                .build();

        HttpResponse<String> res = null;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response.getWriter().write("Notification Service Response: " + res.body());
    }
}
