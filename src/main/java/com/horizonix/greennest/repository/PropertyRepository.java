package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Changed to: Containing (partial match) + IgnoreCase (A=a)
    List<Property> findByCityContainingIgnoreCase(String city);

    List<Property> findByPriceLessThanEqual(Double price);

    List<Property> findByCityContainingIgnoreCaseAndPriceLessThanEqual(String city, Double price);
}