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

    // 1. Show the "Add Property" Page
    @GetMapping("/owner/add-property")
    public String showAddPropertyForm(Model model, Principal principal) {

        // Get the logged-in user
        String email = principal.getName();
        User user = userService.findByEmail(email);

        // SECURITY CHECK: Is this user allowed to upload?
        if (!userService.canUploadProperty(user)) {
            return "redirect:/?error=not-verified"; // Redirect if not verified
        }

        model.addAttribute("property", new Property());
        return "owner/add-property"; // Matches the HTML file name
    }

    // 2. Handle the Form Submission
    @PostMapping("/owner/add-property")
    public String saveProperty(@ModelAttribute Property property,
                               @RequestParam("image") MultipartFile image,
                               Principal principal) {
        try {
            // Set the owner of the property
            String email = principal.getName();
            User user = userService.findByEmail(email);
            property.setOwner(user);

            // Save via Service
            propertyService.saveProperty(property, image);

            return "redirect:/properties?success"; // Go to list page on success

        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/owner/add-property?error=upload-failed";
        }
    }

    // 3. Show All Properties (Student View)
    @GetMapping("/properties")
    public String listProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "property-list";
    }
}