package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.Role; // Import Role enum
import com.horizonix.greennest.entity.User;
import com.horizonix.greennest.service.BookingService;
import com.horizonix.greennest.service.PropertyService;
import com.horizonix.greennest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import java.security.Principal;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    // Handles the "Request Visit" button click
    @PostMapping("/book/{propertyId}")
    public String handleBookingRequest(@PathVariable Long propertyId, Principal principal) {
        try {
            // 1. Get the current logged-in user
            String email = principal.getName();
            User student = userService.findByEmail(email);

            // 2. Security Check: Only STUDENTS can book
            if (student.getRole() != Role.STUDENT) {
                return "redirect:/property-list?error=notAuthorized";
            }

            // 3. Find the property being booked
            Property property = propertyService.getPropertyById(propertyId);
            if (property == null) {
                return "redirect:/property-list?error=propertyNotFound";
            }

            // 4. Save the booking
            bookingService.createVisitRequest(property, student);

            // 5. Success Redirect
            return "redirect:/properties?success=requestSent";

        } catch (IllegalStateException e) {
            // Catches "Already Booked" errors from Service
            return "redirect:/properties?error=alreadyBooked";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/properties?error=unknown";
        }
    }
}