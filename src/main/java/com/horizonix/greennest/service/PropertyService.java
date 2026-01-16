package com.horizonix.greennest.service;

import com.horizonix.greennest.entity.Property;
import com.horizonix.greennest.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> searchProperties(String city, Double price) {
        // Case 1: Search by BOTH City and Price
        if (city != null && !city.isEmpty() && price != null) {
            // FIX: Updated to match the Repository's new method name
            return propertyRepository.findByCityContainingIgnoreCaseAndPriceLessThanEqual(city, price);
        }
        // Case 2: Search by City ONLY
        else if (city != null && !city.isEmpty()) {
            // FIX: Updated to match the Repository's new method name
            return propertyRepository.findByCityContainingIgnoreCase(city);
        }
        // Case 3: Search by Price ONLY
        else if (price != null) {
            return propertyRepository.findByPriceLessThanEqual(price);
        }

        // Case 4: No filters, return ALL
        return propertyRepository.findAll();
    }
}