package com.example.consul.controllers.mvc;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@ControllerAdvice
public class AdviceController {
    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public String renderError(Model model, Exception e) {
        model.addAttribute("errorCode", e.getClass().getName());
        model.addAttribute("errorMessage",  e.getMessage());
        return "error";
    }
}
