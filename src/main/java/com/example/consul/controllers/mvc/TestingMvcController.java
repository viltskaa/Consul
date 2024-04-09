package com.example.consul.controllers.mvc;

import com.example.consul.services.WB_Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class TestingMvcController {
    @GetMapping("")
    public String mainPage() {
        return "reportDetail";
    }
}
