package com.example.demo;

import com.example.demo.entity.SecurityKey;
import com.example.demo.repository.SecurityKeyRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final SecurityKeyRepository repository;

    public HomeController(SecurityKeyRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }


}
