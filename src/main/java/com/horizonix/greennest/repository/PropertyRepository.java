package com.horizonix.greennest.repository;

import com.horizonix.greennest.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByCity(String city);

    List<Property> findByPriceLessThanEqual(Double price);

    List<Property> findByCityAndPriceLessThanEqual(String city, Double price);
}