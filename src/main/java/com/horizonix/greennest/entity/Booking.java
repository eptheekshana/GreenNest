package com.horizonix.greennest.entity;

import jakarta.persistence.*;
import lombok.Data;
// ✅ CORRECT IMPORT:
import com.horizonix.greennest.entity.Property;

@Entity
@Data
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // other fields

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
}