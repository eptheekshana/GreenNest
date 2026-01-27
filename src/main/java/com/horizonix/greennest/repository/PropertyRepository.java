package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Page<Property> findByLocationContainingIgnoreCaseAndPriceLessThanEqual(String location, Double price, Pageable pageable);
}