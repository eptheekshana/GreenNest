package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // 1. Search Logic: Find by Location (partial match) and Price (less than or equal)
    Page<Property> findByLocationContainingIgnoreCaseAndPriceLessThanEqual(String location, Double price, Pageable pageable);

    // 2. Owner Dashboard: Find all properties belonging to a specific Owner
    List<Property> findByOwner(User owner);

    // 3. Public Feed: Find ONLY properties where the Owner is Verified
    List<Property> findByOwner_IsVerifiedTrueOrderByCreatedAtDesc();
}