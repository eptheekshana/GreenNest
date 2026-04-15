package com.horizonix.nboard.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "property_photos", joinColumns = @JoinColumn(name = "property_id"))
    @Column(name = "photo_url", length = 500)
    private List<String> photoUrls = new ArrayList<>();

    // Status field (PENDING, APPROVED, REJECTED)
    // Default is "PENDING" so it's hidden until Admin approves
    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;

    // Relationship: A property belongs to one Owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PrePersist
    @PreUpdate
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null || status.isBlank()) {
            status = "PENDING";
        }

        if (photoUrls == null) {
            photoUrls = new ArrayList<>();
        }

        photoUrls.removeIf(url -> url == null || url.isBlank());

        if (!photoUrls.isEmpty()) {
            imageUrl = photoUrls.get(0);
        } else if (imageUrl != null && !imageUrl.isBlank()) {
            photoUrls = new ArrayList<>(Collections.singletonList(imageUrl));
        }
    }

    @Transient
    public List<String> getResolvedPhotoUrls() {
        if (photoUrls != null && !photoUrls.isEmpty()) {
            return photoUrls;
        }

        if (imageUrl != null && !imageUrl.isBlank()) {
            return Collections.singletonList(imageUrl);
        }

        return Collections.emptyList();
    }

    @Transient
    @SuppressWarnings("unused")
    public String getPrimaryPhotoUrl() {
        List<String> resolvedPhotoUrls = getResolvedPhotoUrls();
        return resolvedPhotoUrls.isEmpty() ? null : resolvedPhotoUrls.get(0);
    }

    // --- Constructors ---
    public Property() {
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING"; // Ensure new properties are always pending
    }
}