package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.PropertyService;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;

@Controller
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @GetMapping("/search")
    public String searchProperties(@RequestParam(required = false) String location,
                                   @RequestParam(required = false) Double price,
                                   @RequestParam(defaultValue = "0") int page,
                                   Model model) {
        PageRequest pageable = PageRequest.of(page, 6);
        Page<Property> propertyPage = propertyService.searchProperties(location, price, pageable);

        model.addAttribute("properties", propertyPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", propertyPage.getTotalPages());
        model.addAttribute("location", location);
        model.addAttribute("price", price);
        return "listings";
    }
}