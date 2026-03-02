package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.Booking;
import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.User;
import com.horizonix.nboard.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;


    //Logic for "Request Visit": Saves a new booking with PENDING status.

    public void createVisitRequest(Property property, User student) {
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setStudent(student);
        booking.setStatus("PENDING"); // Initial status as required
        booking.setRequestDate(LocalDateTime.now());

        bookingRepository.save(booking);
    }


     // Fetches bookings for the Owner's dashboard.

    public List<Booking> getRequestsForOwner(User owner) {
        return bookingRepository.findByPropertyOwner(owner);
    }


     //Fetches bookings for the Student's "My Bookings" page.

    public List<Booking> getBookingsForStudent(User student) {
        return bookingRepository.findByStudent(student);
    }

    // Update booking status (ACCEPTED or REJECTED)
    public void updateBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + bookingId));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}
