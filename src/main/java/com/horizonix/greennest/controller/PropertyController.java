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

    @Autowired private PropertyService propertyService;
    @Autowired private UserService userService;

    // --- 1. PUBLIC: LIST ALL PROPERTIES ---
    @GetMapping("/properties")
    public String listProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "listings"; // Reusing your listings.html
    }

    // --- 1B. STUDENT/BUYER: LIST ALL PROPERTIES (After Login) ---
    @GetMapping("/user/listings")
    public String studentListings(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "user/listings"; // User-specific listings page
    }

    // --- 2. PUBLIC: PROPERTY DETAILS ---
    @GetMapping({"/property/{id}", "/property-details/{id}"})
    public String showPropertyDetails(@PathVariable Long id, Model model) {
        Property property = propertyService.getPropertyById(id);
        model.addAttribute("property", property);
        return "property-details"; // You need to create this HTML file
    }

    // --- 3. PUBLIC: SEARCH ---
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

    // --- 4. OWNER: DASHBOARD ---
    @GetMapping("/owner/dashboard")
    public String showOwnerDashboard(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        model.addAttribute("myProperties", propertyService.getPropertiesByOwner(user));
        model.addAttribute("ownerName", user.getFullName());
        model.addAttribute("isVerified", user.isVerified());
        return "owner/dashboard";
    }

    // --- 5. OWNER: SHOW ADD FORM ---
    @GetMapping("/owner/add-property")
    public String showAddPropertyForm(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());

        // Check if Owner is Verified
        if (!user.isVerified()) {
            return "redirect:/owner/dashboard?error=not-verified";
        }

        model.addAttribute("property", new Property());
        return "owner/add-property";
    }

    // --- 6. OWNER: SAVE PROPERTY ---
    @PostMapping("/owner/add-property")
    public String saveProperty(@ModelAttribute Property property,
                               @RequestParam("image") MultipartFile image,
                               Principal principal) {
        try {
            User user = userService.findByEmail(principal.getName());
            property.setOwner(user);
            propertyService.saveProperty(property, image);
            return "redirect:/owner/property-submitted";
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/owner/add-property?error=upload-failed";
        }
    }

    // --- 7. OWNER: DELETE PROPERTY ---
    @GetMapping("/owner/delete/{id}")
    public String deleteProperty(@PathVariable Long id, Principal principal) {
        // Optional: Add check to ensure only the owner can delete their own property
        propertyService.deleteProperty(id);
        return "redirect:/owner/dashboard?deleted";
    }

    // --- OWNER: PROPERTY SUBMITTED CONFIRMATION ---
    @GetMapping("/owner/property-submitted")
    public String showPropertySubmitted() {
        return "owner/property-submitted";
    }
}