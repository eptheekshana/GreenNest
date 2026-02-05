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

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    // --- 1. SHOW DASHBOARD ---
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        List<Property> myProperties = propertyService.getPropertiesByOwner(user);
        model.addAttribute("properties", myProperties);
        model.addAttribute("ownerName", user.getFullName());
        return "owner/dashboard"; // Matches owner/dashboard.html
    }

    // --- 2. SHOW ADD FORM ---
    @GetMapping("/add-property")
    public String showAddPropertyForm(Model model) {
        model.addAttribute("property", new Property());
        return "owner/add-property";
    }

    // --- 3. HANDLE FORM SUBMISSION ---
    @PostMapping("/add-property")
    public String addProperty(@ModelAttribute Property property,
                              @RequestParam("image") MultipartFile file,
                              Principal principal) {
        try {
            // 1. Get Logged-in Owner
            User owner = userService.findByEmail(principal.getName());

            // 2. Set Owner MANUALLY here (Since Service doesn't take it anymore)
            property.setOwner(owner);

            // 3. Call Service
            propertyService.saveProperty(property, file);

            return "redirect:/owner/dashboard?success";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/owner/add-property?error";
        }
    }

    // --- 4. DELETE PROPERTY ---
    @GetMapping("/delete/{id}")
    public String deleteProperty(@PathVariable Long id, Principal principal) {
        // Security Check: Ensure the logged-in user actually owns this property!
        Property property = propertyService.getPropertyById(id);
        String currentUsername = principal.getName();

        if (property.getOwner().getEmail().equals(currentUsername)) {
            propertyService.deleteProperty(id);
        }

        return "redirect:/owner/dashboard";
    }
}