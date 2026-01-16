package com.horizonix.greennest.service;

import com.horizonix.greennest.repository.PropertyRepository;
import org.hibernate.mapping.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    public List<Property> searchProperties(String city, Double price) {
        if (city != null && !city.isEmpty() && price != null) {
            return propertyRepository.findByCityAndPriceLessThanEqual(city, price);
        } else if (city != null && !city.isEmpty()) {
            return propertyRepository.findByCity(city);
        } else if (price != null) {
            return propertyRepository.findByPriceLessThanEqual(price);
        }
        return propertyRepository.findAll();
    }
}