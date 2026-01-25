package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Booking;
import com.horizonix.greennest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    //Custom query method to find booking by property owners
     List<Booking> findByPropertyOwner(User owner);


    // Jpa Repository already provides  save(), findAll(), delete(), etc. for free!
}