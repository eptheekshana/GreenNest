package com.horizonix.nboard.controller;

import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.service.PropertyService;
import com.horizonix.nboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    // Inject PropertyService to handle property logic
    @Autowired
    private PropertyService propertyService;

    // --- 1. ADMIN DASHBOARD (Shows Pending Owners AND Properties) ---
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        // Fetch Pending Owners
        List<User> pendingOwners = userService.getPendingOwners();
        model.addAttribute("pendingOwners", pendingOwners);

        // Fetch Pending Properties
        // (Make sure you added 'getPendingProperties()' to PropertyService in the previous step)
        List<Property> pendingProperties = propertyService.getPendingProperties();
        model.addAttribute("pendingProperties", pendingProperties);

        return "admin/dashboard";
    }

    // --- 2. ACTION: APPROVE OWNER ---
    @PostMapping("/approve/{id}")
    public String approveOwner(@PathVariable Long id) {
        userService.approveOwner(id);
        return "redirect:/admin/dashboard?ownerSuccess";
    }

    // --- 3. APPROVE PROPERTY ---
    @PostMapping("/approve-property/{id}")
    public String approveProperty(@PathVariable Long id) {
        propertyService.approveProperty(id);
        return "redirect:/admin/dashboard?propSuccess";
    }
}