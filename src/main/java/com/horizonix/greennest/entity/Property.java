package com.horizonix.greennest.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String location;

    // Store the full URL from DigitalOcean Spaces
    @Column(length = 500)
    private String imageUrl;

    // Status field (PENDING, APPROVED, REJECTED)
    // Default is "PENDING" so it's hidden until Admin approves
    @Column(nullable = false)
    private String status = "PENDING";

    private LocalDateTime createdAt;

    // Relationship: A property belongs to one Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }
    }

    // --- Constructors ---
    public Property() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING"; // Ensure new properties are always pending
    }
}