package com.horizonix.nboard.controller;

import com.horizonix.nboard.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private PropertyService propertyService;


    @GetMapping("/")
    public String showHomePage(Model model) {

        model.addAttribute("properties", propertyService.getAllProperties());

        return "index"; // Looks for index.html in templates folder
    }
}