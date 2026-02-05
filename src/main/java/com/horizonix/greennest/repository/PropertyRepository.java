package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // 1. For the Public Page (Only Verified Owners)
    List<Property> findByOwner_IsVerifiedTrueOrderByCreatedAtDesc();

    // 2. For the Owner Dashboard (All their properties)
    List<Property> findByOwner(User owner);

    // 3. For Search functionality (Location + Price)
    Page<Property> findByLocationContainingIgnoreCaseAndPriceLessThanEqual(
            String location,
            Double price,
            Pageable pageable
    );
}