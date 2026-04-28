package com.horizonix.nboard.repository;

import com.horizonix.nboard.entity.Booking;
import com.horizonix.nboard.entity.Property;
import com.horizonix.nboard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Finds bookings for a student (My Bookings)
    List<Booking> findByStudent(User student);

    // Prevents duplicate bookings
    boolean existsByStudentAndProperty(User student, Property property);

    // Finds bookings for a specific single property
    List<Booking> findByProperty(Property property);

    // Finds all bookings for ALL properties belonging to a specific Owner
    List<Booking> findByPropertyOwner(User owner);
}