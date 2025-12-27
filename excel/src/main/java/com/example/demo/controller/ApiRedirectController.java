package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ApiRedirectController {

    // keep backward compatibility: redirect /api/keys -> /api/v1/keys
    @GetMapping("/api/keys")
    public void redirectKeys(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/keys");
    }

    @GetMapping("/api/keys/search")
    public void redirectSearch(HttpServletResponse response) throws IOException {
        response.sendRedirect("/api/v1/keys/search");
    }
}
