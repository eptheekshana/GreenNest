package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.PropertyService;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/owner/add-property")
    public String showAddPropertyForm(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        if (!userService.canUploadProperty(user)) {
            return "redirect:/?error=not-verified";
        }
        model.addAttribute("property", new Property());
        return "owner/add-property";
    }

    @PostMapping("/owner/add-property")
    public String saveProperty(@ModelAttribute Property property,
                               @RequestParam("image") MultipartFile image,
                               Principal principal) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            property.setOwner(user);
            propertyService.saveProperty(property, image);
            return "redirect:/properties?success";
        } catch (IOException e) {
            return "redirect:/owner/add-property?error=upload-failed";
        }
    }

    @GetMapping("/properties")
    public String listProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "property-list";
    }

    @GetMapping("/search")
    public String searchProperties(@RequestParam(required = false) String location,
                                   @RequestParam(required = false) Double price,
                                   Model model) {
        model.addAttribute("properties", propertyService.searchProperties(location, price));
        return "property-list";
    }
}