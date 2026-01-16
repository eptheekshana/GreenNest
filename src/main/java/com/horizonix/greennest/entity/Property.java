package com.horizonix.greennest.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @Column(columnDefinition = "TEXT") // Allows long descriptions
    private String description;

    @NotNull(message = "Rent price is required")
    @Min(value = 1000, message = "Price must be at least 1000")
    private Double price;

    @NotBlank(message = "City is required")
    private String city;

    private String address; // Full address for the map

    @Column(name = "distance_km")
    private Double distanceToUni;

    @Column(name = "has_ac")
    private boolean hasAc;

    @Column(name = "image_url")
    private String imageUrl; // Stores the filename

    // RELATIONSHIP: Many properties belong to One Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}