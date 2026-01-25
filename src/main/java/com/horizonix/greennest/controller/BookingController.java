package com.horizonix.greennest.controller;

import com.horizonix.greennest.entity.Property;
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
        // 1. Get the current logged-in student
        String email = principal.getName();
        User student = userService.findByEmail(email);

        // 2. Find the property being booked
        Property property = propertyService.getPropertyById(propertyId);

        // 3. Save the booking via your service
        bookingService.createVisitRequest(property, student);

        // 4. Redirect to a success page or the listings
        return "redirect:/property-list?success=requestSent";
    }
}
