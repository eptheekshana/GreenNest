package com.horizonix.greennest.controller;

import com.horizonix.greennest.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @GetMapping("/listings")
    public String showListingsPage(@RequestParam(required = false) String city,
                                   @RequestParam(required = false) Double price,
                                   Model model) {
        model.addAttribute("properties", propertyService.searchProperties(city, price));
        return "listings";
    }
}