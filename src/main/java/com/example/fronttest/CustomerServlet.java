package com.example.fronttest;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.net.http.*;
import java.net.URI;

@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5003/"))
                .build();

        HttpResponse<String> res = null;
        try {
            res = client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        response.getWriter().write("Customer Service Response: " + res.body());
    }
}

