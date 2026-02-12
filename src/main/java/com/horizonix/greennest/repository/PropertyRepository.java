package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // --- 1. PUBLIC PAGE: Get "APPROVED" properties only ---
    // (Replaces the old 'findByOwner_IsVerifiedTrue' method)
    List<Property> findByStatusOrderByCreatedAtDesc(String status);

    // --- 2. ADMIN DASHBOARD: Get "PENDING" properties ---
    // (You can use the method above, or this specific one)
    List<Property> findByStatus(String status);

    // --- 3. OWNER DASHBOARD: Get all properties for the logged-in owner ---
    List<Property> findByOwner(User owner);

    // --- 4. SEARCH: Must also filter by "APPROVED" status ---
    // We added 'AndStatus' to the end so unapproved items don't appear in search
    Page<Property> findByLocationContainingIgnoreCaseAndPriceLessThanEqualAndStatus(
            String location,
            Double price,
            String status, // You will pass "APPROVED" here
            Pageable pageable
    );
}