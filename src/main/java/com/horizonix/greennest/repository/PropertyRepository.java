package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find all properties created by a specific owner
    List<Property> findByOwner(User owner);

    // Search properties by location (for Student Search)
    List<Property> findByLocationContainingIgnoreCase(String location);
}