package com.horizonix.greennest.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ ADD THIS FIELD IF MISSING
    private LocalDateTime requestDate;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    // Status: PENDING, APPROVED, REJECTED
    private String status;

    // --- Constructors ---
    public Booking() {
        this.requestDate = LocalDateTime.now(); // Default to now
        this.status = "PENDING";
    }

    public Booking(User student, Property property) {
        this.student = student;
        this.property = property;
        this.requestDate = LocalDateTime.now();
        this.status = "PENDING";
    }

    // --- Getters & Setters ---

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}