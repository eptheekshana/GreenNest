package com.horizonix.nboard.controller;

import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.service.PropertyService;
import com.horizonix.nboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
public class PropertyController {

    private static final Logger logger = LoggerFactory.getLogger(PropertyController.class);

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
        List<Property> featuredProperties = propertyService.getAllProperties()
                .stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .toList();

        model.addAttribute("property", property);
        model.addAttribute("featuredProperties", featuredProperties);
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
        model.addAttribute("isEdit", false);
        return "owner/add-property";
    }

    // --- 6. OWNER: SAVE PROPERTY ---
    @PostMapping("/owner/add-property")
    public String saveProperty(@ModelAttribute Property property,
                               @RequestParam(value = "images", required = false) MultipartFile[] images,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());

            // Double-check verification status before saving
            if (!user.isVerified()) {
                redirectAttributes.addFlashAttribute("error", "You must be verified by admin before adding properties.");
                return "redirect:/owner/dashboard?error=not-verified";
            }

            List<MultipartFile> uploadedImages = images == null ? List.of() : Arrays.asList(images);

            // Validate images
            boolean hasAtLeastOneImage = uploadedImages.stream().anyMatch(image -> image != null && !image.isEmpty());
            if (!hasAtLeastOneImage) {
                redirectAttributes.addFlashAttribute("error", "Please upload at least one property photo.");
                return "redirect:/owner/add-property?error=no-images";
            }

            property.setOwner(user);
            propertyService.saveProperty(property, uploadedImages);
            redirectAttributes.addFlashAttribute("submitSuccess", true);
            redirectAttributes.addFlashAttribute("propertyTitle", property.getTitle());
            return "redirect:/owner/property-submitted";
        } catch (IOException e) {
            logger.error("Failed to upload property photos", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload photos: " + e.getMessage());
            return "redirect:/owner/add-property?error=upload-failed";
        } catch (Exception e) {
            logger.error("Unexpected error while saving property", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/owner/add-property?error=unknown";
        }
    }

    // --- 7. OWNER: DELETE PROPERTY ---
    @GetMapping("/owner/delete/{id}")
    public String deleteProperty(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            Property property = propertyService.getPropertyById(id);
            
            // Security check: Verify the owner owns this property
            if (!property.getOwner().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You cannot delete this property.");
                return "redirect:/owner/dashboard?error=not-owner";
            }
            
            propertyService.deleteProperty(id);
            redirectAttributes.addFlashAttribute("success", "Property deleted successfully.");
        } catch (Exception e) {
            logger.error("Error deleting property", e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete property: " + e.getMessage());
        }
        return "redirect:/owner/dashboard";
    }

    // --- OWNER: PROPERTY SUBMITTED CONFIRMATION ---
    @GetMapping("/owner/property-submitted")
    public String showPropertySubmitted() {
        return "owner/property-submitted";
    }

    // --- 8. OWNER: SHOW EDIT FORM ---
    @GetMapping("/owner/edit/{id}")
    public String showEditPropertyForm(@PathVariable Long id, Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        Property property = propertyService.getPropertyById(id);

        if (!property.getOwner().getId().equals(user.getId())) {
            return "redirect:/owner/dashboard?error=not-owner";
        }

        model.addAttribute("property", property);
        model.addAttribute("isEdit", true);
        return "owner/add-property";
    }

    // --- 9. OWNER: UPDATE PROPERTY ---
    @PostMapping("/owner/edit/{id}")
    public String updateProperty(@PathVariable Long id,
                                 @ModelAttribute Property property,
                                 @RequestParam(value = "images", required = false) MultipartFile[] images,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(principal.getName());
            Property existing = propertyService.getPropertyById(id);

            if (!existing.getOwner().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "You cannot edit this listing.");
                return "redirect:/owner/dashboard?error=not-owner";
            }

            List<MultipartFile> uploadedImages = images == null ? List.of() : Arrays.asList(images);
            propertyService.updateProperty(existing, property, uploadedImages);
            redirectAttributes.addFlashAttribute("submitSuccess", true);
            redirectAttributes.addFlashAttribute("propertyTitle", property.getTitle());
            return "redirect:/owner/property-submitted";
        } catch (IOException e) {
            logger.error("Failed to upload property photos", e);
            redirectAttributes.addFlashAttribute("error", "Failed to upload photos: " + e.getMessage());
            return "redirect:/owner/edit/" + id + "?error=upload-failed";
        } catch (Exception e) {
            logger.error("Unexpected error while updating property", e);
            redirectAttributes.addFlashAttribute("error", "An error occurred: " + e.getMessage());
            return "redirect:/owner/edit/" + id + "?error=unknown";
        }
    }
}