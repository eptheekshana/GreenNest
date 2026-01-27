package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByLocationContainingIgnoreCaseAndPriceLessThanEqual(String location, Double price);

}